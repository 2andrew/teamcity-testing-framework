package com.example.teamcity.ui.elements;

import com.codeborne.selenide.SelenideElement;
import lombok.Getter;

@Getter
public class ProjectElement extends  BasePageElement {
    private SelenideElement name;
    private SelenideElement link;
    private SelenideElement button;

    public ProjectElement(SelenideElement element) {
        super(element);
        this.name = find("span[class*='MiddleEllipsis__searchable']");
        this.link = find("a");
        this.button = find("[data-test='ring-dropdown'] button");
    }
}
