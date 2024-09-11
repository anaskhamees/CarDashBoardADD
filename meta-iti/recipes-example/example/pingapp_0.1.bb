SUMMARY = "bitbake-layers recipe"
DESCRIPTION = "Recipe created by bitbake-layers"
LICENSE="MIT"
LIC_FILES_CHKSUM="file://home/ahmed/poky/meta/files/common-licenses/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"
FILESPATH:append=":${THISDIR}:"

python do_display_banner(){
    bb.plain("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!")
    bb.plain("!!!!!!!!!!!!!! Hello, My name is Ahmed Abdalla !!!!!!!!!!!!!!!!!")
    bb.plain("!!!!!!!!!!!!!! This is ping application !!!!!!!!!!!!!!!!!")
    bb.plain("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!")
}

LIC_FILES_CHKSUM="file://${COREBASE}/meta/files/common-licenses/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"


SRC_URI="file://task_app.py"
PYPI_PACKAGE = "RPi.GPIO"

#do_compile(){
#    #oe_runmake -C ${B}
#    #${CC} -static ${CFLAGS} ${WORKDIR}/main.c -o ${B}/myapp
#}
#
do_install(){
    mkdir -p ${D}${bindir}
    #install -m 0755 ${S}/task_app.py ${D}${bindir}/task_app
    cp ${WORKDIR}/task_app.py ${D}/usr/bin
}



# Ensure Python runtime dependency
DEPENDS = "python3"
RDEPENDS:${PN} = "python3"

addtask display_banner before do_build
