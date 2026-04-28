package com.thedrofdoctoring.synthetics.capabilities.interfaces;

import com.thedrofdoctoring.synthetics.core.data.types.body.installables.AppliedAugmentInstance;
import com.thedrofdoctoring.synthetics.core.data.types.body.installables.BodyPart;
import com.thedrofdoctoring.synthetics.core.data.types.body.installables.BodySegment;
import com.thedrofdoctoring.synthetics.core.data.types.body.installables.IBodyInstallable;

import java.util.Collection;
import java.util.List;
@SuppressWarnings("unused")
public interface IPartManager {

    Collection<BodyPart> installedBodyParts();
    Collection<BodySegment> installedSegments();
    Collection<AppliedAugmentInstance> installedAugments();

    boolean isBodyPartInstalled(BodyPart part);
    boolean isSegmentInstalled(BodySegment segment);
    boolean isAugmentInstalled(AppliedAugmentInstance augment);

    List<IBodyInstallable<?>> replaceBodyPart(BodyPart newPart, boolean updatePlayer);
    List<IBodyInstallable<?>> replaceBodyPart(BodyPart newPart);

    List<IBodyInstallable<?>> replaceSegment(BodySegment newSegment);
    List<IBodyInstallable<?>> replaceSegment(BodySegment newSegment, boolean updatePlayer);

    void removeAugment(AppliedAugmentInstance augment);
    void addAugment(AppliedAugmentInstance augment);


}
