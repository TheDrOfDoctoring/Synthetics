package com.thedrofdoctoring.synthetics.capabilities;

import com.thedrofdoctoring.synthetics.core.data.types.body.BodyPart;
import com.thedrofdoctoring.synthetics.core.data.types.body.BodySegment;

import java.util.Collection;

public interface IPartManager {

    Collection<BodyPart> getInstalledParts();
    Collection<BodySegment> getInstalledSegments();

    boolean isPartInstalled(BodyPart part);
    boolean isSegmentInstalled(BodySegment segment);

    BodyPart replacePart(BodyPart newPart, boolean updatePlayer);
    BodyPart replacePart(BodyPart newPart);

    BodySegment replaceSegment(BodySegment newSegment);
    BodySegment replaceSegment(BodySegment newSegment, boolean updatePlayer);

}
