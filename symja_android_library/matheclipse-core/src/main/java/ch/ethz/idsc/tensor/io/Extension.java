package ch.ethz.idsc.tensor.io;

/** file extensions used by the tensor library
 * 
 * @see ImportHelper
 * @see ExportHelper */
public enum Extension {
  /** uncompressed loss-less image format, no alpha channel */
  BMP, //
  /** table */
  CSV, //
  /** animation format */
  GIF, //
  /** compressed version of another format, for instance csv.gz */
  GZ, //
  /** compressed, lossy image format */
  JPG, //
  /** MATLAB m file */
  M, //
  /** compressed image format with alpha channel */
  PNG, //
  /** text format */
  TXT, //
  /** tensor library specific: vector */
  VECTOR, //
  ;
}
