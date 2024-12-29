package com.xxx.test.api.nrt.apinrt;

import com.xxx.test.api.nrt.apinrt.campaignExecutor.CampaignExecutor;
import com.xxx.test.api.nrt.apinrt.campaignInitializer.CampaignInitializer;
import com.xxx.test.api.nrt.apinrt.model.Campaign;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class CampaignOrchestrator {

    CampaignInitializer CampaignInitializer;
    CampaignExecutor campaignExecutor;

    public CampaignOrchestrator(CampaignInitializer CampaignInitializer, CampaignExecutor campaignExecutor) {
        this.CampaignInitializer = CampaignInitializer;
        this.campaignExecutor = campaignExecutor;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void playCampaign(){
        Campaign campaign = CampaignInitializer.createCampaignFromTestRepository();
        campaignExecutor.executeCampaign(campaign);
    }
}
