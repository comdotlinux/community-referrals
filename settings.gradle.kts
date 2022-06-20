pluginManagement {
    val quarkusPluginVersion: String by settings
    val quarkusPluginId: String by settings
    repositories {
        mavenCentral()
        gradlePluginPortal()
        mavenLocal()
    }
    plugins {
        id(quarkusPluginId) version quarkusPluginVersion
    }
}
rootProject.name="nokri"


plugins {
    id("com.gradle.enterprise") version("3.10.1") // https://plugins.gradle.org/plugin/com.gradle.enterprise
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
        isEnabled = true
        directory = File(rootDir, "build-cache")
        removeUnusedEntriesAfterDays = 30
        logger.info("Enabled Build Cache and Storing it in $rootDir/build-cache")
    }
}