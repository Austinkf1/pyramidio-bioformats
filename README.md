# PyramidIO: image pyramid reader/writer tool. Bio-Formats enhanced version.

This fork features BioFormatsImageReader which provides support for [reading over 100 image formats](https://docs.openmicroscopy.org/bio-formats/latest/supported-formats.html) and potentially converting them to [DZI pyramids](https://en.wikipedia.org/wiki/Deep_Zoom) suitable for using with different viewers, including, but not limited to, [OpenSeadragon](https://openseadragon.github.io) based viewers.

While the reader should work with the all formats supported by Bio-Formats, the preferred, most tested and efficient image input format is tiled TIFF/BigTIFF with LZW or JPEG compression. Please consider converting your images to the mentioned format by using [ImageMagick tool](https://imagemagick.org), the command example:
```
magick convert -verbose -define tiff:tile-geometry=256x256 input_image.xxx -compress jpeg -quality 95 output_image.tif
```
This will produce 256x256 tiled JPEG (lossy) compressed TIFF file.
```
magick convert -verbose -define tiff:tile-geometry=256x256 input_image.xxx -compress lzw output_image.tif
```
This will produce 256x256 tiled LZW (lossless) compressed TIFF file.

Alternatively you can try to use bftools [https://docs.openmicroscopy.org/bio-formats/latest/users/comlinetools/index.html] 

## CLI usage

The CLI allows to build a DZI pyramid from an image.
To start the CLI, one should use `pyramidio-cli-[version].jar` like this:

```
java -jar pyramidio-cli-[version].jar -i my-image.jpg -o (my-output-folder || scheme:///path/file[.tar, .seq])
```

Examples:
```
java -jar pyramidio-cli-[version].jar -i my-image.jpg -o outputfolder
java -jar pyramidio-cli-[version].jar -i my-image.jpg -o file:///tmp/outputfolder.tar
java -jar pyramidio-cli-[version].jar -i my-image.jpg -o s3://my-image-bucket/outputfolder
java -jar pyramidio-cli-[version].jar -i my-image.jpg -o hdfs://localhost:9000/outputfolder
java -jar pyramidio-cli-[version].jar -i my-image.jpg -o hdfs://localhost:9000/outputfolder.tar
java -jar pyramidio-cli-[version].jar -i my-image.jpg -o hdfs://localhost:9000/outputfolder.seq

```

To get the list of all the options, one can type:
```
java -jar pyramidio-cli-[version].jar -h
```

## Library usage

### Write a DZI pyramid

To write a DZI pyramid, one should use the gov.nist.isg.pyramidio.ScalablePyramidBuilder class:
```java
ScalablePyramidBuilder spb = new ScalablePyramidBuilder(tileSize, tileOverlap, tileFormat, "dzi");
FilesArchiver archiver = new DirectoryArchiver(outputFolder);
BioFormatsImageReader pir = new BioFormatsImageReader(imageFile);
spb.buildPyramid(pir, "pyramidName", archiver, parallelism);
```
Currently the available `FilesArchiver`s are:
* `DirectoryArchiver`: save files in a directory on the filesystem.
* `TarArchiver`: save files in a tar file on the filesystem.
* `SequenceFileArchiver`: save files in a Hadoop sequence file.
* `HdfsArchiver`: save files on a HDFS filesystem.
* `TarOnHdfsArchiver`: save files in a tar file created on a HDFS filesystem.
* `S3Archiver`: save files to a folder on a S3 bucket.

As for the `PartialImageReader`s:
* `BioFormatsImageReader`: read an image using Bio-Formats library.
* `BufferedImageReader`: read an image from the disk and store it in RAM.
* `DeepZoomImageReader`: read a DZI pyramid.
* `MistStitchedImageReader`: read a [MIST](https://github.com/NIST-ISG/MIST) translation vector.

### Read a DZI pyramid

To read a DZI pyramid, one should use the `DeepZoomImageReader` class:
```java
File dziFile = new File("my-image.dzi");
DeepZoomImageReader reader = new DeepZoomImageReader(dziFile);
BufferedImage wholeImageZoom0_01 = reader.getWholeImage(0.01);
BufferedImage regionAtZoom0_1 = reader.getSubImage(
    new Rectangle(x, y, width, height), 0.1);
```

## Disclaimer:

This software was developed at the National Institute of Standards and Technology by employees of the Federal Government in the course of their official duties. Pursuant to title 17 Section 105 of the United States Code this software is not subject to copyright protection and is in the public domain. This software is an experimental system. NIST assumes no responsibility whatsoever for its use by other parties, and makes no guarantees, expressed or implied, about its quality, reliability, or any other characteristic. We would appreciate acknowledgement if the software is used.
