package com.thedrofdoctoring.synthetics.capabilities.interfaces;

import com.thedrofdoctoring.synthetics.abilities.IBodyInstallable;
import com.thedrofdoctoring.synthetics.core.data.types.body.parts.BodyPart;
import com.thedrofdoctoring.synthetics.core.data.types.body.parts.BodySegment;

import java.util.Collection;
import java.util.List;
@SuppressWarnings("unused")
public interface IPartManager {

    Collection<BodyPart> getInstalledParts();
    Collection<BodySegment> getInstalledSegments();

    boolean isPartInstalled(BodyPart part);
    boolean isSegmentInstalled(BodySegment segment);

    List<IBodyInstallable<?>> replacePart(BodyPart newPart, boolean updatePlayer);
    List<IBodyInstallable<?>> replacePart(BodyPart newPart);

    List<IBodyInstallable<?>> replaceSegment(BodySegment newSegment);
    List<IBodyInstallable<?>> replaceSegment(BodySegment newSegment, boolean updatePlayer);

}
