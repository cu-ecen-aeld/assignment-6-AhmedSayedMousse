#   LICENSE
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://LICENSE;md5=f098732a73b5f6f3430472f5b094ffdb"

inherit module

SRC_URI = "git://git@github.com/cu-ecen-aeld/assignment-7-AhmedSayedMousse.git;protocol=ssh;branch=main \
	   file://scull_module_start_stop_daemon.sh \
	  "

# Modify these as desired
PV = "1.0+git${SRCPV}"
SRCREV = "9400a89b2873c655cdda1baa081b6f7f54a6ddba"

S = "${WORKDIR}/git"

EXTRA_OEMAKE:append:task-install = " -C ${STAGING_KERNEL_DIR} M=${S}/scull"
EXTRA_OEMAKE += "KERNELDIR=${STAGING_KERNEL_DIR} INSTALL_MOD_DIR="""

RPROVIDES:${PN} += "kernel-module-scull"

KERNEL_MODULE_AUTOLOAD += "scull"
FILES:${PN} += "${bindir}/scull_load \
		${bindir}/scull_unload \
		${sysconfdir}/init.d \
		"
inherit update-rc.d
INITSCRIPT_NAME = "scull_module_start_stop_daemon.sh"
INITSCRIPT_PARAMS = "defaults 99"
		
do_install (){
	unset CFLAGS CPPFLAGS CXXFLAGS LDFLAGS
	oe_runmake DEPMOD=echo MODLIB="${D}${nonarch_base_libdir}/modules/${KERNEL_VERSION}" \
	           INSTALL_FW_PATH="${D}${nonarch_base_libdir}/firmware" \
	           CC="${KERNEL_CC}" LD="${KERNEL_LD}" \
	           O=${STAGING_KERNEL_BUILDDIR} \
	           ${MODULES_INSTALL_TARGET}

	if [ ! -e "${B}/${MODULES_MODULE_SYMVERS_LOCATION}/Module.symvers" ] ; then
		bbwarn "Module.symvers not found in ${B}/${MODULES_MODULE_SYMVERS_LOCATION}"
		bbwarn "Please consider setting MODULES_MODULE_SYMVERS_LOCATION to a"
		bbwarn "directory below B to get correct inter-module dependencies"
	else
		install -Dm0644 "${B}/${MODULES_MODULE_SYMVERS_LOCATION}"/Module.symvers ${D}${includedir}/${BPN}/Module.symvers
		# Module.symvers contains absolute path to the build directory.
		# While it doesn't actually seem to matter which path is specified,
		# clear them out to avoid confusion
		sed -e 's:${B}/::g' -i ${D}${includedir}/${BPN}/Module.symvers
	fi
	bbwarn "Custom Part"
	install -d ${D}${bindir}
	install -m 0755 ${S}/scull/scull_load ${D}${bindir}/scull_load
	install -m 0755 ${S}/scull/scull_unload ${D}${bindir}/scull_unload
	
	install -d ${D}${sysconfdir}/init.d
	install -m 0755 ${WORKDIR}/scull_module_start_stop_daemon.sh ${D}${sysconfdir}/init.d
}
