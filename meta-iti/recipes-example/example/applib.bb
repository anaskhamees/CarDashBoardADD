SUMMARY = "Systemd service for myscript"
DESCRIPTION = "This recipe installs and enables a systemd service for the myscript"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=3b83ef96387f14655fc854ddc3c6bd57"

SRC_URI = "file://myscript.service \
            file://.profile \
            file://script \
            file://Temp.service \
            file://current.service \
        "

inherit systemd

SYSTEMD_SERVICE_${PN} = "myscript.service"

do_install() {
    # Install the systemd service file
    install -d ${D}${systemd_system_unitdir}
    install -m 0644 ${WORKDIR}/myscript.service ${D}${systemd_system_unitdir}
    install -m 0644 ${WORKDIR}/Temp.service ${D}${systemd_system_unitdir}
    install -m 0644 ${WORKDIR}/current.service ${D}${systemd_system_unitdir}
    install -m 0644 ${WORKDIR}/.profile ${D}$/home/root/
    install -m 0644 ${WORKDIR}/.script ${D}$/home/root/
}

FILES_${PN} += "${systemd_system_unitdir}/myscript.service"

# Enable the service by default
SYSTEMD_AUTO_ENABLE = "enable"