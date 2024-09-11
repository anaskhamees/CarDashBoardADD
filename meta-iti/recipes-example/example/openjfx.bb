SUMMARY = "Recipe to load openjfx.zip into the target image"
DESCRIPTION = "This recipe copies the openjfx.zip file into the /opt directory on the target image."
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=0835ade698e0bcf8506ecda2f7b4f302"


# Specify where the source file is located
SRC_URI = "file://openjfx.zip"

# Specify the destination directory where the zip file will be copied
S = "${WORKDIR}"


# Installation steps
do_install() {
    # Install the zip file into /opt directory on the target image
    install -d ${D}/opt
    install -m 0644 ${WORKDIR}/openjfx.zip ${D}/opt/
}