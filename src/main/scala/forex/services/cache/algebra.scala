package forex.services.cache

trait Algebra[F[_], K, V] {
  def getIfValid(key: K): F[Option[V]]
  def set(newCache: Map[K, V]): F[Unit]
}
