package com.example.teamcity.ui.pages.admin;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import lombok.Getter;

import java.time.Duration;

import static com.codeborne.selenide.Selenide.$;

public class CreateBuildConfigPage extends CreateBasePage {
    private static final String BUILD_TYPE_SHOW_MODE = "createBuildTypeMenu";

    private SelenideElement buildTypeNameInput = $("#buildTypeName");
    private SelenideElement buildTypeNameInputError = $("#error_buildTypeName");

    public static CreateBuildConfigPage open(String projectId) {
        return Selenide.open(CREATE_URL.formatted(projectId, BUILD_TYPE_SHOW_MODE), CreateBuildConfigPage.class);
    }

    public CreateBuildConfigPage createForm(String url) {
        baseCreateForm(url);
        return this;
    }

    public void setupBuild(String buildName) {
        buildTypeNameInput.val(buildName);
        submitButton.click();
    }

    public String getBuildTypeNameInputErrorText() {
        buildTypeNameInputError.shouldBe(Condition.visible, Duration.ofSeconds(5));
        return buildTypeNameInputError.getText();
    }
}