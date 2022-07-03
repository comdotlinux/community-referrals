plugins {
    kotlin("jvm") version "1.6.21"
    kotlin("plugin.allopen") version "1.6.21"
    id("io.quarkus")
    id("com.vaadin")
}

repositories {
    mavenCentral()
    mavenLocal()
}

val quarkusPlatformGroupId: String by project
val quarkusPlatformArtifactId: String by project
val quarkusPlatformVersion: String by project
val vaadinVersion: String by project
val vaadinQuarkusVersion: String by project

dependencies {
    implementation("io.quarkus:quarkus-hibernate-validator")
    implementation(enforcedPlatform("${quarkusPlatformGroupId}:${quarkusPlatformArtifactId}:${quarkusPlatformVersion}"))

    implementation(enforcedPlatform("com.vaadin:vaadin-bom:$vaadinVersion"))

    // Vaadin
    implementation("com.vaadin:vaadin-core")
    implementation("com.vaadin:vaadin-core-jandex")
    implementation("com.vaadin:vaadin-quarkus:$vaadinQuarkusVersion")

    implementation("io.quarkus:quarkus-keycloak-authorization")
    implementation("io.quarkus:quarkus-security")
    implementation("io.quarkus:quarkus-oidc")

    implementation("io.quarkus:quarkus-websockets")
    implementation("io.quarkus:quarkus-container-image-docker")
    implementation("io.quarkus:quarkus-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("io.quarkus:quarkus-arc")
    implementation("io.quarkus:quarkus-jdbc-postgresql")
    implementation("io.quarkus:quarkus-hibernate-orm-panache-kotlin")
    testImplementation("io.quarkus:quarkus-junit5")
    testImplementation("io.rest-assured:rest-assured")
}

group = "com.linux"
version = "1.0.0-SNAPSHOT"

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

tasks.quarkusDev {
    compilerOptions {
        compiler("kotlin")
//            .args(mutableListOf("-Werror"))
    }
}

/*tasks.quarkusBuild {
    nativeArgs {
        "additional-build-args" to "--allow-incomplete-classpath,--initialize-at-run-time=com.vaadin.flow.server.communication.PushRequestHandler"
    }
}*/

allOpen {
    annotation("javax.ws.rs.Path")
    annotation("javax.enterprise.context.ApplicationScoped")
    annotation("io.quarkus.test.junit.QuarkusTest")
    annotation("com.vaadin.flow.router.Route")
    annotation("com.vaadin.flow.router.Route")
    annotation("javax.persistence.Entity")
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions.jvmTarget = JavaVersion.VERSION_17.toString()
    kotlinOptions.javaParameters = true
}

// TODO: De-duplicate following tasks with a single configurable task

task("herokuLogin") {
    group = "referrals"
    description="Login to heroku"
    doLast {
        exec {
            executable="heroku"
            args="container:login".split(" ")
            environment("HEROKU_API_KEY", System.getenv("HEROKU_API_KEY"))
        }
    }
}

task("herokuPushImage") {
    group = "referrals"
    description="Push Created docker Image to Heroku"
    dependsOn("herokuLogin", tasks.findByName("build"))
    doLast {
        exec {
            executable="docker"
            args="push registry.heroku.com/community-referrals/web".split(" ")
            environment("HEROKU_API_KEY", System.getenv("HEROKU_API_KEY"))
        }
    }
}

task("herokuDeploy") {
    group = "referrals"
    description="Trigger Deployment to heroku"
    dependsOn("herokuPushImage")
    doLast {
        exec {
            executable="heroku"
            args="container:release web --app community-referrals".split(" ")
            environment("HEROKU_API_KEY", System.getenv("HEROKU_API_KEY"))
        }
    }
}

task("herokuShowLogs") {
    group = "referrals"
    description="Show logs from heroku container"
    doLast {
        exec {
            executable="heroku"
            args="logs --app community-referrals --num 500 --force-colors".split(" ")
            environment("HEROKU_API_KEY", System.getenv("HEROKU_API_KEY"))
        }
    }
}