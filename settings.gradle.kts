pluginManagement {
    val quarkusPluginVersion: String by settings
    val quarkusPluginId: String by settings
    val vaadinVersion: String by settings
    repositories {
        mavenCentral()
        gradlePluginPortal()
        mavenLocal()
        maven { setUrl("https://maven.vaadin.com/vaadin-prereleases") }
    }
    plugins {
        id(quarkusPluginId) version quarkusPluginVersion
        id("com.vaadin") version vaadinVersion
    }
}
rootProject.name="community-referrals"


plugins {
    id("com.gradle.enterprise") version("3.10.2") // https://plugins.gradle.org/plugin/com.gradle.enterprise
}

gradleEnterprise {
    buildScan {
        termsOfServiceUrl = "https://gradle.com/terms-of-service"
        termsOfServiceAgree = "yes"
        capture {
            isTestLogging = true
            isBuildLogging = true
            isTaskInputFiles = true
        }
        publishAlways()
    }
}

buildCache {
    local {
        isEnabled = false
        directory = File(rootDir, "build-cache")
        removeUnusedEntriesAfterDays = 30
        logger.info("Enabled Build Cache and Storing it in $rootDir/build-cache")
    }
}