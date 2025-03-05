package com.example.teamcity.ui.elements;

import com.codeborne.selenide.SelenideElement;
import lombok.Getter;

@Getter
public class BuildConfigElement extends  BasePageElement {
    private SelenideElement name;
    private SelenideElement link;
    private SelenideElement button;
    private SelenideElement run_button;

    public BuildConfigElement(SelenideElement element) {
        super(element);
        this.name = find("span[class*='MiddleEllipsis__searchable']");
        this.link = find("a");
        this.button = find("[data-test='ring-dropdown'] button");
        this.run_button = find("[data-test='run-build']");
    }
}
