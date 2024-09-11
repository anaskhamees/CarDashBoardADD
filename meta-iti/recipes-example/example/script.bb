SUMMARY = "A simple Python script"
DESCRIPTION = "This recipe installs a Python script to /usr/bin"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=3b83ef96387f14655fc854ddc3c6bd57"

SRC_URI = "file://temp.py \
            file://current.py
            "

S = "${WORKDIR}"

inherit python3native

do_install() {
    install -d ${D}${bindir}
    install -m 0755 ${WORKDIR}/myscript.py ${D}${bindir}/myscript
}

FILES_${PN} += "${bindir}/myscript"