pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
        maven {
            url = uri("https://maven.myket.ir")
            metadataSources {
                gradleMetadata()
                mavenPom()
                artifact()
            }
        }
        maven { url = uri("https://hub.megan.ir/maven") }
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://jitpack.io") }
        maven {
            url = uri("https://maven.myket.ir")
            metadataSources {
                gradleMetadata()
                mavenPom()
                artifact()
            }
        }
        maven { url = uri("https://hub.megan.ir/maven") }

    }
}

rootProject.name = "My Shatel Mobile Widget"
include(":app")
