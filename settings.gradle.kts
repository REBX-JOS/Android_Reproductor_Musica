pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
        maven {
            url = uri("https://maven.google.com")
        }
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.PREFER_PROJECT)
    repositories {
        mavenCentral()
        maven {
            url = uri("https://maven.google.com")
        }
    }
}

rootProject.name = "Android_Reproductor_Musica"
include(":app")
