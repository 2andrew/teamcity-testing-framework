package com.example.teamcity.ui.elements;

import com.codeborne.selenide.SelenideElement;
import lombok.Getter;

@Getter
public class BuildDetailsElement extends BasePageElement {
    private SelenideElement build_number;
    private SelenideElement build_status;

    public BuildDetailsElement(SelenideElement element) {
        super(element);
        this.build_number = find("div[class*='Build__number'] span[class*='MiddleEllipsis__searchable'] ");
        this.build_status = find("div[class*='Build__status'] span[class*='MiddleEllipsis__searchable'] ");
    }
}
