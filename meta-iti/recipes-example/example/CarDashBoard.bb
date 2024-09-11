# cardashboard_git.bb
SUMMARY = "Fetch and build Java project from GitHub repository"
DESCRIPTION = "This recipe fetches the Car Dashboard Java project from GitHub and builds it."
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=0835ade698e0bcf8506ecda2f7b4f302"

# GitHub repository URL
SRC_URI = "git://github.com/anaskhamees/CarDashBoardADD.git;protocol=https"

# Branch to fetch (use 'main' if not specified)
SRCREV = "99d62b9d7eea8b3b15a0fcad938f83cd8af7e9a8"

# Specify the name of the Java project directory after fetching
S = "${WORKDIR}/git"

do_install() {
    # Create target directory
    install -d ${D}/opt/cardashboard

    # Copy the JAR file to the target directory
    cp ${S}/path/to/CarDashboard.jar ${D}/opt/cardashboard/
}
