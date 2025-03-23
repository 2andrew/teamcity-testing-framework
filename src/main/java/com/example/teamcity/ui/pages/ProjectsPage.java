package com.example.teamcity.ui.pages;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import com.example.teamcity.ui.elements.ProjectElement;
import com.example.teamcity.ui.elements.SidebarRow;
import io.qameta.allure.Step;

import java.time.Duration;
import java.util.List;

import static com.codeborne.selenide.Selenide.*;

public class ProjectsPage extends BasePage {
    private static final String PROJECTS_URL = "/favorite/projects";

    private ElementsCollection projectElements = $$("div[class*='Subproject__container']");

    private SelenideElement header = $(".MainPanel__router--gF > div");

    private SelenideElement sidebarSearchField = $("#search-projects");
    private ElementsCollection sidebarRowElements = $$("[role='grid'] [role='row']");

    public SelenideElement placeholderText = $("div.ProjectsTree__placeholder--Wf span");

    public SelenideElement FAVORITE_SECTION = $x("//*[text()='FAVORITES']");

    public ProjectsPage() {
        header.shouldBe(Condition.visible, BASE_WAITING);
    }

    @Step("Open projects page")
    public static ProjectsPage open() {
        return Selenide.open(PROJECTS_URL, ProjectsPage.class);
    }

    public List<ProjectElement> getProjects() {
        return generatePageElements(projectElements, ProjectElement::new);
    }

    public void searchProjects(String name) {
        sidebarSearchField.val(name);
        FAVORITE_SECTION.shouldNotBe(Condition.visible, Duration.ofSeconds(3));
    }

    public List<SidebarRow> getSidebarRows() {
        return generatePageElements(sidebarRowElements, SidebarRow::new);
    }
}
