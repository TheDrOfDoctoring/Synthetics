package com.thedrofdoctoring.synthetics.capabilities;

import com.thedrofdoctoring.synthetics.body.abilities.IBodyInstallable;
import com.thedrofdoctoring.synthetics.capabilities.serialisation.ISyncable;
import com.thedrofdoctoring.synthetics.core.data.types.research.ResearchNode;

public interface IResearchManager extends ISyncable {


    boolean hasResearched(IBodyInstallable installable);

    boolean hasResearched(ResearchNode node);

    void addResearched(ResearchNode node);

    boolean canResearch(ResearchNode node);

    void removedResearched(ResearchNode node);

    void handleResearchCosts(ResearchNode node);
}
