/*
  Fury, version 0.4.0. Copyright 2018-19 Jon Pretty, Propensive Ltd.

  The primary distribution site is: https://propensive.com/

  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
  in compliance with the License. You may obtain a copy of the License at

      http://www.apache.org/licenses/LICENSE-2.0

  Unless required  by applicable  law or  agreed to  in writing,  software  distributed  under the
  License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
  express  or  implied.  See  the  License for  the specific  language  governing  permissions and
  limitations under the License.
 */
package fury.core
import scala.annotation.tailrec

case class DirectedGraph[T](connections: Map[T, Set[T]]) {

  def remove(element: T): DirectedGraph[T] = {
    val pointingTo = connections(element)
    val noFromEdge = connections - element
    DirectedGraph(noFromEdge.mapValues { x =>
      if (x(element)) x ++ pointingTo - element else x
    })
  }

  def subgraph(verticesToLeave: Set[T]): DirectedGraph[T] = {
    val toCut = connections.keySet &~ verticesToLeave
    toCut.foldRight(this) { (element, graph) =>
      graph.remove(element)
    }
  }

  def neighbours(start: T): Set[T] =
    connections.getOrElse(start, Set())

  def findCycle(start: T): Option[List[T]] = {
    @tailrec
    def findCycleHelper(queue: List[(T, List[T])], finished: Set[T]): Option[List[T]] =
      queue match {
        case List() => None
        case (vertex, trace) :: tail => {
          val commonElement = trace.toSet.intersect(neighbours(vertex)).headOption
          commonElement match {
            case Some(element) => Some(trace ++ List(vertex, element))
            case None =>
              findCycleHelper(
                  tail ++ neighbours(vertex).diff(finished).toList.map((_, trace :+ vertex)),
                  finished + vertex)
          }
        }
      }

    findCycleHelper(List((start, List())), Set())
  }

  def hasCycle(start: T): Boolean = findCycle(start).isDefined

  def allDescendants(start: T): Either[List[T], Set[T]] = {
    @tailrec
    def allDescendantsHelper(stack: List[T], ans: Set[T]): Set[T] =
      stack match {
        case List()       => ans
        case head :: tail => allDescendantsHelper(neighbours(head).toList ++ tail, ans + head)
      }

    findCycle(start) match {
      case Some(cycle) => Left(cycle)
      case None        => Right(neighbours(start).flatMap(c => allDescendantsHelper(List(c), Set())))
    }
  }

}
