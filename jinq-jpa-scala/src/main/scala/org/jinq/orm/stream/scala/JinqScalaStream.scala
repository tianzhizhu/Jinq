package org.jinq.orm.stream.scala

import java.math.BigDecimal
import java.math.BigInteger

trait JinqScalaStream[T] {
  def where(fn: (T) => Boolean) : JinqScalaStream[T]
  def where(fn: (T, InQueryStreamSource) => Boolean) : JinqScalaStream[T]
  
  def select[U](fn: (T) => U) : JinqScalaStream[U]
  def select[U](fn: (T, InQueryStreamSource) => U) : JinqScalaStream[U]
  
  def join[U](fn: (T) => JinqScalaStream[U]) : JinqScalaStream[(T,U)]
  def join[U](fn: (T, InQueryStreamSource) => JinqScalaStream[U]) : JinqScalaStream[(T,U)]
  
  def leftOuterJoin[U](fn: (T) => JinqScalaStream[U]) : JinqScalaStream[Tuple2[T,U]]
//  def group[U,V](groupingFn: (T) => U, valueFn: (U, JinqScalaStream[T]) => V) : JinqScalaStream[Tuple2[U, V]]    
  def count() : Long
  def sumInteger(fn: (T) => Int) : Long
  def sumLong(fn: (T) => Long) : Long
  def sumDouble(fn: (T) => Double) : Double
  def sumBigDecimal(fn: (T) => BigDecimal) : BigDecimal
  def sumBigInteger(fn: (T) => BigInteger) : BigInteger
  def max[V/*<:java.lang.Comparable[V]*/](fn: (T) => V) : V  // TODO: Use Ordered instead?
  def min[V/*<:java.lang.Comparable[V]*/](fn: (T) => V) : V // V should be Comparable, but we can't do that since Scala's primitive values aren't Comparable, and implicit conversion can get confused sometimes
  def avg[V:Numeric](fn: (T) => V) : Double
  def sortedBy[V/*<:java.lang.Comparable[V]*/](fn: (T) => V) : JinqScalaStream[T] // V should be Comparable, but we can't do that since Scala's primitive values aren't Comparable, and implicit conversion can get confused sometimes
  def sortedDescendingBy[V/*<:java.lang.Comparable[V]*/](fn: (T) => V) : JinqScalaStream[T]
  def limit(n: Long) : JinqScalaStream[T]  
  def skip(n: Long) : JinqScalaStream[T]  
  def distinct() : JinqScalaStream[T]  
//  def aggregate[U,V](fn1: (JinqScalaStream[T]) => U, fn2: (JinqScalaStream[T]) => V) : Tuple2[U,V]
  def setHint(name: String, value: Object) : JinqScalaStream[T]

  def toIterator() : Iterator[T]
  def toList() : List[T] 
  def getOnlyValue() : T
}

object JinqScalaStream {
  def of[T](value : T) : JinqScalaStream[T] = {
    throw new IllegalArgumentException("Jinq for Scala currently only allows streams to be used when they can be converted into database queries. This usage does not form a valid database query.");
  }
}