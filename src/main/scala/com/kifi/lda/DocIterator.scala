package com.kifi.lda

import scala.io.Source
import scala.collection.mutable.ListBuffer

trait DocIterator {
  def hasNext: Boolean
  def next: Doc
  def gotoHead(): Unit
  def getPosition(): Int
}

class OnDiskDocIterator(fileName: String) extends DocIterator {
  private var lineIter = Source.fromFile(fileName).getLines
  private var p = 0
  
  def hasNext = lineIter.hasNext
  
  def next = {
    p += 1
    Doc(p, lineIter.next.split(" ").map{_.toInt})
  }
  
  def gotoHead() = {
    lineIter = Source.fromFile(fileName).getLines
    p = 0
  }
  
  def getPosition(): Int = p
}

class InMemoryDocIterator(fileName: String) extends DocIterator with Logging{
  implicit val intOrd = Ordering[Int]
  private val lines = {
    log.info("init in-memory doc iterator...")
    
    val lines = ListBuffer.empty[Array[Int]]
    var lineIter = Source.fromFile(fileName).getLines
    while(lineIter.hasNext){
      val terms = lineIter.next.split(" ").map{_.toInt}
      lines += terms
    }
    log.info("in-memory doc iterator initialized")
    lines
  }
  
  private var p = 0
  private var iter = lines.iterator
  
  def hasNext = iter.hasNext
  
  def next = { 
    p += 1
    Doc(p, iter.next)
  }
  
  def gotoHead() = {
    p = 0
    iter = lines.iterator
  }
  
  def getPosition(): Int = p
}
