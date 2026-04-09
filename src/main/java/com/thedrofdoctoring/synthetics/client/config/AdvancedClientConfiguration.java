package com.thedrofdoctoring.synthetics.client.config;

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.thedrofdoctoring.synthetics.Synthetics;
import com.thedrofdoctoring.synthetics.client.screens.ability_wheel.AbilityWheel;
import com.thedrofdoctoring.synthetics.client.screens.ability_wheel.WheelManager;
import com.thedrofdoctoring.synthetics.core.data.types.body.ability.Ability;
import com.thedrofdoctoring.synthetics.networking.from_client.ServerboundActivateAbilityPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Holder;
import net.minecraft.resources.RegistryOps;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class AdvancedClientConfiguration {

    private static final Gson GSON = new Gson();

    private final Map<UUID, ClientConfigurationData> levelData = new HashMap<>();

    private @Nullable UUID currentLevelUUID;


    public void saveLevelConfig(@NotNull Level level) {
        if(currentLevelUUID == null || levelData.getOrDefault(currentLevelUUID, null) == null) {
            Synthetics.LOGGER.warn("Unable to save client configuration data as no level UUID is available");
            return;
        }
        ClientConfigurationData configData = levelData.get(currentLevelUUID);
        try (final PrintWriter printWriter = new PrintWriter(new OutputStreamWriter(new FileOutputStream(getFileForUUID(currentLevelUUID)), StandardCharsets.UTF_8))) {
            ClientConfigurationData.CODEC.codec()
                    .encodeStart(RegistryOps.create(JsonOps.INSTANCE, level.registryAccess()), configData)
                    .ifError(err -> Synthetics.LOGGER.error("Error saving data for client config: {}", err))
                    .ifSuccess(    json -> printWriter.print(GSON.toJson(json)));

        } catch (Exception exception) {
            Synthetics.LOGGER.error("Failed to save client configuration data", exception);
        }

    }

    public void loadLevelConfig(@NotNull Level level) {

        if(currentLevelUUID == null) {
            Synthetics.LOGGER.warn("Unable to retrieve client configuration data as no level UUID is available");
            return;
        }

        File wheelsFile = getFileForUUID(currentLevelUUID);
        final DynamicOps<JsonElement> dynamicOps = RegistryOps.create(JsonOps.INSTANCE, level.registryAccess());
        try (BufferedReader bufferedReader = Files.newReader(wheelsFile, Charsets.UTF_8)) {
            ClientConfigurationData data = ClientConfigurationData.CODEC.codec()
                    .parse(dynamicOps, JsonParser.parseReader(bufferedReader))
                    .resultOrPartial()
                    .orElseThrow();
            this.levelData.put(currentLevelUUID, data);

        } catch (Exception e) {
            this.levelData.put(currentLevelUUID, ClientConfigurationData.createEmpty());
            Synthetics.LOGGER.warn("Failed to read client configuration data file", e);
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


    public void setWheelForLevel(Level level, List<AbilityWheel> wheels) {
        ClientConfigurationData data = getLevelData(currentLevelUUID);
        if(data != null) {
            data.wheelManager.setWheelsForLevel(wheels);
        }
    }

    public @Nullable WheelManager currentLevelWheelManager() {
        ClientConfigurationData data = getLevelData(currentLevelUUID);
        return data == null ? null : data.wheelManager;
    }

    private @Nullable ClientConfigurationData getLevelData(UUID uuid) {
        return this.levelData.get(uuid);
    }

    public List<AbilityWheel> levelWheels() {

        WheelManager manager = currentLevelWheelManager();
        if(manager == null) {
            return Collections.emptyList();
        }
        return manager.levelWheels();
    }

    public @Nullable AbilityKeyManager currentKeyManager() {
        ClientConfigurationData data = getLevelData(currentLevelUUID);
        return data == null ? null : data.keyManager;
    }

    public void toggleAction(int index) {
        AbilityKeyManager manager = currentKeyManager();
        if(manager != null && Minecraft.getInstance().getConnection() != null) {
            Holder<Ability> toToggle = manager.getBoundAbilityHolder(index);
            if(toToggle != null) {
                Minecraft.getInstance().getConnection().send(new ServerboundActivateAbilityPacket(toToggle));
            }
        }
    }

    private record ClientConfigurationData(@NotNull WheelManager wheelManager, @NotNull AbilityKeyManager keyManager) {

        public static final MapCodec<ClientConfigurationData> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                WheelManager.CODEC.fieldOf("wheels").forGetter(ClientConfigurationData::wheelManager),
                AbilityKeyManager.CODEC.fieldOf("hot_keys").forGetter(ClientConfigurationData::keyManager)
        ).apply(instance, ClientConfigurationData::new));

        public static ClientConfigurationData createEmpty() {
            return new ClientConfigurationData(new WheelManager(Collections.emptyList()), new AbilityKeyManager(Collections.emptyMap()));
        }
    }
}
