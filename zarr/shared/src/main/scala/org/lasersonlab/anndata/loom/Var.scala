package org.lasersonlab.anndata.loom

import org.lasersonlab.zarr.Empty
import org.lasersonlab.zarr.dtype.DataType
import org.lasersonlab.zarr.dtype.DataType.string
import shapeless.the

case class Var(
  index: Long,
  accession: String,
  gene: Short,
  logCV: Double,
  logMean: Double,
  selected: Long,
  total: Double,
  valid: Long
)

object Var {
  val empty = Empty[Var]()
}