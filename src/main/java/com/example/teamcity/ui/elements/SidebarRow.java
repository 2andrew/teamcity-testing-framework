package com.example.teamcity.ui.elements;

import com.codeborne.selenide.SelenideElement;
import lombok.Getter;

@Getter
public class SidebarRow extends  BasePageElement {
    private SelenideElement name;
    private SelenideElement button;

    public SidebarRow(SelenideElement element) {
        super(element);
        this.name = find("span[class*='ProjectsTreeItem__name']");
        this.button = find("button");
    }
}
