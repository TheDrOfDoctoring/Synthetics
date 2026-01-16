package com.thedrofdoctoring.synthetics.client.screens.ability_wheel;

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import com.thedrofdoctoring.synthetics.Synthetics;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.RegistryOps;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * Right now, we only support Ability Wheels, but if that needs to change in the future it shouldn't be too difficult.
 */
public class WheelManager {

    private final Map<UUID, List<AbilityWheel>> levelWheelMap = new HashMap<>();
    private @Nullable UUID currentLevelUUID;


    private static final Gson GSON = new Gson();

    public @NotNull List<AbilityWheel> loadWheelsForCurrentLevel(Level level) {
        if(currentLevelUUID == null) {
            return Collections.emptyList();
        }

        if(levelWheelMap.containsKey(currentLevelUUID)) {
            return new ArrayList<>(levelWheelMap.get(currentLevelUUID));
        }

        File wheelsFile = getFileForUUID(currentLevelUUID);
        final DynamicOps<JsonElement> dynamicOps = RegistryOps.create(JsonOps.INSTANCE, level.registryAccess());
        try (BufferedReader bufferedReader = Files.newReader(wheelsFile, Charsets.UTF_8)) {
            var wheelOpt = AbilityWheel.LIST_CODEC.parse(dynamicOps, JsonParser
                    .parseReader(bufferedReader))
                    .result();
            if(wheelOpt.isPresent()) {
                levelWheelMap.put(currentLevelUUID, wheelOpt.get());
                return wheelOpt.get();
            }
        } catch (Exception e) {
            return Collections.emptyList();
        }
        return Collections.emptyList();
    }

    public void saveWheelsToFile(Level level) {
        if(currentLevelUUID == null) return;
        if(levelWheelMap.getOrDefault(currentLevelUUID, Collections.emptyList()).isEmpty()) {
            return;
        }
        List<AbilityWheel> wheels = levelWheelMap.get(currentLevelUUID);
        try (final PrintWriter printWriter = new PrintWriter(new OutputStreamWriter(new FileOutputStream(getFileForUUID(currentLevelUUID)), StandardCharsets.UTF_8))) {
            AbilityWheel.LIST_CODEC
                    .encodeStart(RegistryOps.create(JsonOps.INSTANCE, level.registryAccess()), wheels)
                    .ifError(err -> Synthetics.LOGGER.error("Error saving wheel for UUID: {}", currentLevelUUID))
                    .ifSuccess(    json -> printWriter.print(GSON.toJson(json)));

        } catch (Exception exception) {
            Synthetics.LOGGER.error("Failed to save wheel configuration", exception);
        }
    }

    public void setWheelForLevel(Level level, List<AbilityWheel> wheels) {
        if(currentLevelUUID != null) {
            levelWheelMap.put(currentLevelUUID, wheels);
        }
    }

    private File getFileForUUID(UUID uuid) {
        File dir = new File(Minecraft.getInstance().gameDirectory, "config/synthetics");
        //noinspection ResultOfMethodCallIgnored
        dir.mkdirs();
        return new File(dir, uuid.toString() + ".json");
    }

    public void setLevelUUID(UUID uuid) {
        this.currentLevelUUID = uuid;
    }

    public UUID currentLevelUUID() {
        return currentLevelUUID;
    }



}
