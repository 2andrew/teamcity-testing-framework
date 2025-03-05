package com.example.teamcity.ui.pages;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import com.example.teamcity.ui.elements.BuildConfigElement;
import com.example.teamcity.ui.elements.BuildDetailsElement;

import java.util.List;
import java.util.Optional;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

public class ProjectsBuildsPage extends BasePage {
    private static final String PROJECTS_URL = "/project/%s?mode=builds";

    private ElementsCollection buildsConfigElements = $$("div[class*='BuildTypes__item']");
    private ElementsCollection buildsDetailsElements = $$("div[class*='BuildDetails__container']");
    private SelenideElement header = $("[data-test='overview-header']");

    public ProjectsBuildsPage() {
        header.shouldBe(Condition.visible, BASE_WAITING);
    }

    public static ProjectsBuildsPage open(String projectId) {
        return Selenide.open(PROJECTS_URL.formatted(projectId), ProjectsBuildsPage.class);
    }

    public List<BuildConfigElement> getBuildConfigElements() {
        return generatePageElements(buildsConfigElements, BuildConfigElement::new);
    }

    public void runBuild(String buildName) {
        Optional<BuildConfigElement> buildRow = this.getBuildConfigElements().stream()
                .filter(build -> build.getName().text().equals(buildName))
                .findFirst();

        buildRow.ifPresent(build -> {
            build.getRun_button().click();
        });
    }

    public List<BuildDetailsElement> getBuildsDetailsElements() {
        return generatePageElements(buildsDetailsElements, BuildDetailsElement::new);
    }

    public BuildDetailsElement getBuildDetailsElement(int seqNumber) {
        Optional<BuildDetailsElement> buildRow = this.getBuildsDetailsElements().stream()
                .skip(seqNumber - 1)
                .findFirst();
        return buildRow.orElse(null);
    }
}
