package org.apache.flink.contrib.tensorflow.detector.inception

import java.nio.file.Paths

import org.apache.flink.contrib.tensorflow.detector.inception.InceptionModel._
import org.apache.flink.contrib.tensorflow.streaming._
import org.apache.flink.streaming.api.functions.source.FileProcessingMode._
import org.apache.flink.streaming.api.scala._
import org.tensorflow.contrib.scala._
import resource._

import scala.collection.mutable
import scala.concurrent.duration._

/**
  * Identifies specific image sequences, based on the 'inception5h' model of TF
  */
object DetectorImages {

  type Image = Array[Byte]

  def main(args: Array[String]) {
    val env = StreamExecutionEnvironment.getExecutionEnvironment
    env.setParallelism(1)

    if (args.length < 2) {
      System.out.println("Incorrect arguments\nUsage: DetectorImages <model-dir> <image-dir>")
      System.exit(1)
    }
    val modelPath = Paths.get(args.toSeq.head).toUri
    val imagePath = args.toSeq.tail.head

    // 1. read input files as images
    val imageStream = env
      .readFile(new ImageInputFormat, imagePath, PROCESS_CONTINUOUSLY, (1 second).toMillis)

    // 2. label the image using the TensorFlow 'inception' model
    implicit val inceptionModel = new InceptionModel(modelPath)

    val labelStream = imageStream.mapWithModel(inceptionModel) { (in, model) =>
      val labeled = managed(in._2.toTensor.taggedAs[ImageTensor])
        .flatMap(img => model.label(img))
        .acquireAndGet(label => label.toTextLabels())

      val labels = labeled.head.labels

      val data = labels(0)

      val accuracy = (math rint data._1 * 10000) / 100
      val prediccion = data._2

      println("\n-----------------------------------------")
      if(accuracy>75)
        println(s"New image detected. Analyzing...\nThe model predicts with $accuracy% accuracy that it is a $prediccion")
      else {
        println(s"New image detected. Analyzing...\nThe model has doubts. These are his predictions:")


        for (i <- 0 to 2) {

          val data = labels(i)

          val accuracy = (math rint data._1 * 10000) / 100
          val prediccion = data._2

          println(s"\t$prediccion: $accuracy%")
        }
      }
      println("-----------------------------------------")


    }


    // execute program
    env.execute("DetectorImages")
  }

  def labeled(image: LabeledImage, label: String, confidence: Float = .90f): Boolean = {
    image.labels.exists(l => l._2.equalsIgnoreCase(label) && l._1 >= confidence)
  }

  case class AccessDenied(pattern: mutable.Map[String, LabeledImage])
  case class AccessGranted(first: LabeledImage, second: LabeledImage, third: LabeledImage) {
    override def toString: String = s"AccessGranted(${(first.labels.head, second.labels.head, third.labels.head)})"
  }

}

