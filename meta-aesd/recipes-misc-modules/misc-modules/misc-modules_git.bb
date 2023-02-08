#   LICENSE
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://LICENSE;md5=f098732a73b5f6f3430472f5b094ffdb"

SRC_URI = "git://git@github.com/cu-ecen-aeld/assignment-7-AhmedSayedMousse.git;protocol=ssh;branch=main \
   	   file://misc_module_start_stop_daemon.sh \
   	   "

# Modify these as desired
PV = "1.0+git${SRCPV}"
SRCREV = "2a1be60c55baa6bffd429b977729420247ad7677"

inherit module
S = "${WORKDIR}/git"

EXTRA_OEMAKE:append:task-install = " -C ${STAGING_KERNEL_DIR} M=${S}/misc-modules"
EXTRA_OEMAKE += "KERNELDIR=${STAGING_KERNEL_DIR} INSTALL_MOD_DIR="""

RPROVIDES:${PN} += "kernel-module-misc-modules"
KERNEL_MODULE_AUTOLOAD += "hello"

FILES:${PN} += "${bindir}/module_load \
		${bindir}/module_unload \
		${sysconfdir}/init.d \
		"
inherit update-rc.d
INITSCRIPT_NAME = "misc_module_start_stop_daemon.sh"
INITSCRIPT_PARAMS = "defaults 99"

do_install () {
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
	install -m 0755 ${S}/misc-modules/module_load ${D}${bindir}/module_load
	install -m 0755 ${S}/misc-modules/module_unload ${D}${bindir}/module_unload
	
	install -d ${D}${sysconfdir}/init.d
	install -m 0755 ${WORKDIR}/misc_module_start_stop_daemon.sh ${D}${sysconfdir}/init.d
}
