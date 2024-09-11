SUMMARY = "bitbake-layers recipe"
DESCRIPTION = "Recipe created by bitbake-layers"
LICENSE="MIT"
LIC_FILES_CHKSUM="file://home/ahmed/poky/meta/files/common-licenses/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"
FILESPATH:append=":${THISDIR}:"

python do_display_banner(){
    bb.plain("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!")
    bb.plain("!!!!!!!!!!!!!! Hello, My name is Ahmed Abdalla !!!!!!!!!!!!!!!!!")
    bb.plain("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!")
}

LIC_FILES_CHKSUM="file://${COREBASE}/meta/files/common-licenses/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

#SRC_URI="git://github.com/FadyKhalil/DemoApp.git;protocol=https;branch=main"
#SRCREV="720c663c5fd7246b4b42c5205d74db7d9784b5b2"
#S = "${WORKDIR}/git"

SRC_URI="file://main.cpp \
    file://mainwindow.cpp \
        file://mainwindow.h \
            file://mainwindow.ui \
                file://rbpiqt.pro"

#inherit cmake
DEPENDS += "qtbase"
inherit qmake5

S = "${WORKDIR}"

#do_configure(){
#    qmake
#}
do_compile(){
    oe_runmake -C ${B}
    #${CC} -static ${CFLAGS} ${WORKDIR}/main.c -o ${B}/myapp
}
#
do_install(){
    mkdir -p ${D}/usr/bin
    cp ${B}/rbpiqt ${D}/usr/bin
}

addtask display_banner before do_build
