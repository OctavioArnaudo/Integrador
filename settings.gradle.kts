pluginManagement {
	repositories {
		google()
		mavenCentral()
		maven { url = uri("https://repo.spring.io/snapshot") }
		gradlePluginPortal()
	}
}
dependencyResolutionManagement {
	repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
	repositories {
		google()
		mavenCentral()
	}
}

rootProject.name = "FourTwoOne"
include(":app")
