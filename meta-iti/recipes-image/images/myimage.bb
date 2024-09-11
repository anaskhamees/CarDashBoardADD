
SUMMARY = "Ahmed Abdalla created this image recipe."

#IMAGE_FEATURES += "splash  hwcodecs "
#IMAGE_FEATURES += "splash hwcodecs"

IMAGE_FEATURES += "splash package-management x11-base x11-sato ssh-server-dropbear ssh-server-openssh hwcodecs"


IMAGE_INSTALL = "packagegroup-core-boot ${CORE_IMAGE_EXTRA_INSTALL}"

IMAGE_INSTALL:append = " apt bash git python3 python3-pip python3-setuptools linux-firmware-ralink linux-firmware-rtl8192cu linux-firmware-rtl8192su linux-firmware-rpidistro-bcm43455 linux-firmware-rtl8192ce"
IMAGE_INSTALL:append = " example script CarDashBoard openjfx pingapp openssh connman connman-client kernel-module-r8188eu dhcpcd bridge-utils hostapd coreutils iptables"
IMAGE_INSTALL:append = " linux-firmware-bcm43430 bluez5 pi-blaster i2c-tools  wpa-supplicant psplash-raspberrypi coreutils iw raspi-gpio"
#QT configuration
IMAGE_INSTALL:append = " make cmake"
IMAGE_INSTALL:append = " qtbase-tools qtbase qtdeclarative qtimageformats qtmultimedia qtquickcontrols2 qtquickcontrols qtbase-plugins cinematicexperience liberation-fonts qtbase-dev curl wget userland gstreamer1.0-plugins-bad qtsvg"

#IMAGE_INSTALL:append = " gtkperf libxslt oracle-jse-jre "
#IMAGE_INSTALL:append = " openjdk-8"
IMAGE_INSTALL:append = " i2c-tools python3-smbus"
#IMAGE_INSTALL:append = " networkmanager"
#IMAGE_INSTALL:append = " libcanberra libcanberra-gtk libcanberra-gtk3"

IMAGE_ROOTFS_EXTRA_SPACE = "2242880"
#================================

DISTRO_FEATURES:append = " bluez5 bluetooth wifi ipv4 systemd"

IMAGE_INSTALL:append = " apt bash git python3 \
                        python3-pip python3-setuptools \
                        linux-firmware-ralink \
                        linux-firmware-rpidistro-bcm43455 \
                        linux-firmware-rtl8192ce"

IMAGE_INSTALL:append = " pingapp openssh \
                        connman connman-client kernel-module-r8188eu \
                        dhcpcd    coreutils iptables"

IMAGE_INSTALL:append = " linux-firmware-bcm43430 bluez5 pi-blaster i2c-tools  \
                        wpa-supplicant    psplash-raspberrypi coreutils iw raspi-gpio"
#=========================

IMAGE_INSTALL:append = " linux-firmware-iwlwifi "
DISTRO_FEATURES:append = " wifi"
IMAGE_INSTALL:append = " iw wpa-supplicant packagegroup-base module-init-tools"
ENABLE_I2C  = "1"
IMAGE_INSTALL:append = " kernel-modules"
IMAGE_INSTALL:append = " i2c-tools"

IMAGE_INSTALL:append = " wpa-supplicant"
#IMAGE_INSTALL:append = " NetworkManager"
IMAGE_INSTALL:append = " python3-smbus"

#=======================

#Bluetooth and Wi-Fi Tools

MAGE_INSTALL:append = " \
    python3 \
    util-linux \
    bluez5 \
    i2c-tools \
    bridge-utils \
    hostapd \
    iptables \
    wpa-supplicant \
    pi-bluetooth \
    bluez5-testtools \
    udev-rules-rpi \
    linux-firmware \
    iw \
    kernel-modules \
    linux-firmware-ralink \
    linux-firmware-rtl8192ce \
    linux-firmware-rtl8192cu \
    linux-firmware-rtl8192su \
    linux-firmware-rpidistro-bcm43430 \
    linux-firmware-bcm43430 \
    connman \
    connman-client \
    dhcpcd \
    openssh \
    psplash \
    psplash-raspberrypi \
    coreutils \
"
# Additional Features and Configuration for WiFi and Bluetooth
DISTRO_FEATURES:append = " \
    bluez5 \
    bluetooth \
    wifi \
    pi-bluetooth \
    linux-firmware-bcm43430 \
    systemd \
    ipv4 \
"
# usrmerge \
#
MACHINE_FEATURES:append = " \
    bluetooth \
    wifi \
"
#
IMAGE_FEATURES:append = " \
    splash \
"
IMAGE_INSTALL:append = " xserver-xorg xf86-video-fbdev xf86-input-evdev xterm matchbox-wm"

pkg_postinst_ontarget_dpkg-start-stop () {
    dpkg-start-stop
}

pkg_postinst_ontarget_util-linux-rev () {
    # Your post-installation script for util-linux-rev here
}

pkg_postinst_ontarget_coreutils () {
    # Your post-installation script for coreutils here
}



#================================

PACKAGE_CLASSES ?= "package_rpm opkg"
IMAGE_INSTALL:append = " opkg"

ENABLE_UART = "1"
ENABLE_I2C = "1"
ENABLE_WIFI = "1"

inherit core-image

#PACKAGE_EXCLUDE = "rust"

# I used these two lines to build core-image-miniml
#IMAGE_ROOTFS_SIZE ?= "8192"
#IMAGE_ROOTFS_EXTRA_SPACE:append = "${@bb.utils.contains("DISTRO_FEATURES", "systemd", " + 4096", "", d)}"

# this found here /home/ahmed/poky/meta/recipes-sato/images
TOOLCHAIN_HOST_TASK:append = " nativesdk-intltool nativesdk-glib-2.0"
TOOLCHAIN_HOST_TASK:remove:task-populate-sdk-ext = " nativesdk-intltool nativesdk-glib-2.0"

QB_MEM = '${@bb.utils.contains("DISTRO_FEATURES", "opengl", "-m 512", "-m 256", d)}'
QB_MEM:qemuarmv5 = "-m 256"
QB_MEM:qemumips = "-m 256"

