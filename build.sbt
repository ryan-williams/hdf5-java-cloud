
default(
  group("org.lasersonlab"),
  versions(
    hammerlab.          bytes → "1.2.0",
    hammerlab.       cli.base → "1.0.1",
    hammerlab.     math.utils → "2.3.0".snapshot,
    hammerlab.          paths → "1.6.0".snapshot,
    hammerlab.          types → "1.4.0".snapshot,
    hammerlab.shapeless_utils → "1.6.0".snapshot,
    hammerlab.             io → "5.2.0"
  )
)

lazy val cloud =
  project
    .in(new File("cloud"))
    .settings(
      subgroup("cloud", "all")
    )
    .dependsOn(aws, gcp)
    .aggregate(aws, gcp)

lazy val aws =
  project
    .in(new File("cloud") / "aws")
    .settings(
      subgroup("cloud"),
      dep(
        "org.lasersonlab" ^ "s3fs" ^ "2.2.3"
      )
    )

lazy val gcp =
  project
    .in(new File("cloud") / "gcp")
    .settings(
      subgroup("cloud"),
      dep(
        "org.lasersonlab" ^ "google-cloud-nio" ^ "0.55.2-alpha",
        hammerlab.types
      )
    )

lazy val convert = project.settings(
  dep(
    hammerlab.cli.base,
    hammerlab.io,
    hammerlab.paths,
  ),
  // Test-resources include "hidden" (basenames starting with ".") Zarr-metadata files that we need to include on the
  // test classpath for tests to be able to read them
  excludeFilter in sbt.Test := NothingFilter,
  scalacOptions += "-Ypartial-unification"
).dependsOn(
  cloud,
  netcdf,
  zarr andTest
)

lazy val ndarray = crossProject.settings(
  dep(
    cats,
    hammerlab.shapeless_utils,
    shapeless
  ),
  addCompilerPlugin("org.spire-math" % "kind-projector" % "0.9.7" cross CrossVersion.binary),
  scalacOptions += "-Ypartial-unification"
)
lazy val `ndarray.jvm` = ndarray.jvm
lazy val `ndarray.js`  = ndarray.js
lazy val `ndarray-x`   = parent(`ndarray.jvm`, `ndarray.js`)

lazy val netcdf = project.settings(
  dep(
    hammerlab.bytes,
    hammerlab.cli.base,
    hammerlab.io,
    hammerlab.paths,
    hammerlab.types
  )
).dependsOn(
  cloud,
  utils
)


lazy val singlecell = project.settings(
  spark,
  spark.version := "2.2.1",
  dep(
    spark.mllib,
    spark.sql
  )
).dependsOn(
  utils
)

lazy val utils = project.settings(
  dep(
    hammerlab.channel % "1.5.2",

    "org.lasersonlab.thredds" ^ "cdm" ^ "5.0.0",
    "com.novocode" ^ "junit-interface" ^ "0.11" tests
  )
)

lazy val viewerCommon = crossProject.settings()
lazy val viewerCommonJVM = viewerCommon.jvm
lazy val viewerCommonJS  = viewerCommon.js

lazy val viewerServer = project.settings().dependsOn(viewerCommon.jvm)
lazy val viewerClient = project.settings().dependsOn(viewerCommon.js )

lazy val zarr = project.in(new File("zarr/shared")).settings(
  dep(
    circe,
    circe.generic,
    circe.parser,
    hammerlab.io,
    hammerlab.math.utils,
    hammerlab.paths,
    hammerlab.shapeless_utils,
    hammerlab.types,

    sourcecode,

    "org.typelevel" ^^ "kittens" ^ "1.1.0",
    "org.blosc" ^ "jblosc" ^ "1.0.1" snapshot
  ),
  addCompilerPlugin("org.spire-math" % "kind-projector" % "0.9.7" cross CrossVersion.binary),
  scalacOptions += "-Ypartial-unification"
).dependsOn(
  `ndarray.jvm`
)
//lazy val `zarr.jvm` = zarr.jvm
//lazy val `zarr.js`  = zarr.js
//lazy val `zarr-x`   = parent(`zarr.jvm`, `zarr.js`)

lazy val `hdf5-java-cloud` = root(
   cloud,
  `ndarray-x`,
   netcdf,
   singlecell,
   utils,
   zarr
)

