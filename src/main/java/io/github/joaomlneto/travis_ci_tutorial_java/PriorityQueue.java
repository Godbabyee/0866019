//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package java.util;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.function.Consumer;
import java.util.function.Predicate;
import jdk.internal.misc.SharedSecrets;

public class PriorityQueue<E> extends AbstractQueue<E> implements Serializable {
    private static final long serialVersionUID = -7720805057305804111L;
    private static final int DEFAULT_INITIAL_CAPACITY = 11;
    transient Object[] queue;
    int size;
    private final Comparator<? super E> comparator;
    transient int modCount;
    private static final int MAX_ARRAY_SIZE = 2147483639;

    public PriorityQueue() {
        this(11, (Comparator)null);
    }

    public PriorityQueue(int initialCapacity) {
        this(initialCapacity, (Comparator)null);
    }

    public PriorityQueue(Comparator<? super E> comparator) {
        this(11, comparator);
    }

    public PriorityQueue(int initialCapacity, Comparator<? super E> comparator) {
        if (initialCapacity < 1) {
            throw new IllegalArgumentException();
        } else {
            this.queue = new Object[initialCapacity];
            this.comparator = comparator;
        }
    }

    public PriorityQueue(Collection<? extends E> c) {
        if (c instanceof SortedSet) {
            SortedSet<? extends E> ss = (SortedSet)c;
            this.comparator = ss.comparator();
            this.initElementsFromCollection(ss);
        } else if (c instanceof PriorityQueue) {
            PriorityQueue<? extends E> pq = (PriorityQueue)c;
            this.comparator = pq.comparator();
            this.initFromPriorityQueue(pq);
        } else {
            this.comparator = null;
            this.initFromCollection(c);
        }

    }

    public PriorityQueue(PriorityQueue<? extends E> c) {
        this.comparator = c.comparator();
        this.initFromPriorityQueue(c);
    }

    public PriorityQueue(SortedSet<? extends E> c) {
        this.comparator = c.comparator();
        this.initElementsFromCollection(c);
    }

    private static Object[] ensureNonEmpty(Object[] es) {
        return es.length > 0 ? es : new Object[1];
    }

    private void initFromPriorityQueue(PriorityQueue<? extends E> c) {
        if (c.getClass() == PriorityQueue.class) {
            this.queue = ensureNonEmpty(c.toArray());
            this.size = c.size();
        } else {
            this.initFromCollection(c);
        }

    }

    private void initElementsFromCollection(Collection<? extends E> c) {
        Object[] es = c.toArray();
        int len = es.length;
        if (es.getClass() != Object[].class) {
            es = Arrays.copyOf(es, len, Object[].class);
        }

        if (len == 1 || this.comparator != null) {
            Object[] var4 = es;
            int var5 = es.length;

            for(int var6 = 0; var6 < var5; ++var6) {
                Object e = var4[var6];
                if (e == null) {
                    throw new NullPointerException();
                }
            }
        }

        this.queue = ensureNonEmpty(es);
        this.size = len;
    }

    private void initFromCollection(Collection<? extends E> c) {
        this.initElementsFromCollection(c);
        this.heapify();
    }

    private void grow(int minCapacity) {
        int oldCapacity = this.queue.length;
        int newCapacity = oldCapacity + (oldCapacity < 64 ? oldCapacity + 2 : oldCapacity >> 1);
        if (newCapacity - 2147483639 > 0) {
            newCapacity = hugeCapacity(minCapacity);
        }

        this.queue = Arrays.copyOf(this.queue, newCapacity);
    }

    private static int hugeCapacity(int minCapacity) {
        if (minCapacity < 0) {
            throw new OutOfMemoryError();
        } else {
            return minCapacity > 2147483639 ? 2147483647 : 2147483639;
        }
    }

    public boolean add(E e) {
        return this.offer(e);
    }

    public boolean offer(E e) {
        if (e == null) {
            throw new NullPointerException();
        } else {
            ++this.modCount;
            int i = this.size;
            if (i >= this.queue.length) {
                this.grow(i + 1);
            }

            this.siftUp(i, e);
            this.size = i + 1;
            return true;
        }
    }

    public E peek() {
        return this.queue[0];
    }

    private int indexOf(Object o) {
        if (o != null) {
            Object[] es = this.queue;
            int i = 0;

            for(int n = this.size; i < n; ++i) {
                if (o.equals(es[i])) {
                    return i;
                }
            }
        }

        return -1;
    }

    public boolean remove(Object o) {
        int i = this.indexOf(o);
        if (i == -1) {
            return false;
        } else {
            this.removeAt(i);
            return true;
        }
    }

    void removeEq(Object o) {
        Object[] es = this.queue;
        int i = 0;

        for(int n = this.size; i < n; ++i) {
            if (o == es[i]) {
                this.removeAt(i);
                break;
            }
        }

    }

    public boolean contains(Object o) {
        return this.indexOf(o) >= 0;
    }

    public Object[] toArray() {
        return Arrays.copyOf(this.queue, this.size);
    }

    public <T> T[] toArray(T[] a) {
        int size = this.size;
        if (a.length < size) {
            return Arrays.copyOf(this.queue, size, a.getClass());
        } else {
            System.arraycopy(this.queue, 0, a, 0, size);
            if (a.length > size) {
                a[size] = null;
            }

            return a;
        }
    }

    public Iterator<E> iterator() {
        return new PriorityQueue.Itr();
    }

    public int size() {
        return this.size;
    }

    public void clear() {
        ++this.modCount;
        Object[] es = this.queue;
        int i = 0;

        for(int n = this.size; i < n; ++i) {
            es[i] = null;
        }

        this.size = 0;
    }

    public E poll() {
        Object[] es;
        Object result;
        if ((result = (es = this.queue)[0]) != null) {
            ++this.modCount;
            int n;
            E x = es[n = --this.size];
            es[n] = null;
            if (n > 0) {
                Comparator cmp;
                if ((cmp = this.comparator) == null) {
                    siftDownComparable(0, x, es, n);
                } else {
                    siftDownUsingComparator(0, x, es, n, cmp);
                }
            }
        }

        return result;
    }

    E removeAt(int i) {
        Object[] es = this.queue;
        ++this.modCount;
        int s = --this.size;
        if (s == i) {
            es[i] = null;
        } else {
            E moved = es[s];
            es[s] = null;
            this.siftDown(i, moved);
            if (es[i] == moved) {
                this.siftUp(i, moved);
                if (es[i] != moved) {
                    return moved;
                }
            }
        }

        return null;
    }

    private void siftUp(int k, E x) {
        if (this.comparator != null) {
            siftUpUsingComparator(k, x, this.queue, this.comparator);
        } else {
            siftUpComparable(k, x, this.queue);
        }

    }

    private static <T> void siftUpComparable(int k, T x, Object[] es) {
        Comparable key;
        int parent;
        for(key = (Comparable)x; k > 0; k = parent) {
            parent = k - 1 >>> 1;
            Object e = es[parent];
            if (key.compareTo(e) >= 0) {
                break;
            }

            es[k] = e;
        }

        es[k] = key;
    }

    private static <T> void siftUpUsingComparator(int k, T x, Object[] es, Comparator<? super T> cmp) {
        while(true) {
            if (k > 0) {
                int parent = k - 1 >>> 1;
                Object e = es[parent];
                if (cmp.compare(x, e) < 0) {
                    es[k] = e;
                    k = parent;
                    continue;
                }
            }

            es[k] = x;
            return;
        }
    }

    private void siftDown(int k, E x) {
        if (this.comparator != null) {
            siftDownUsingComparator(k, x, this.queue, this.size, this.comparator);
        } else {
            siftDownComparable(k, x, this.queue, this.size);
        }

    }

    private static <T> void siftDownComparable(int k, T x, Object[] es, int n) {
        Comparable<? super T> key = (Comparable)x;

        int child;
        for(int half = n >>> 1; k < half; k = child) {
            child = (k << 1) + 1;
            Object c = es[child];
            int right = child + 1;
            if (right < n && ((Comparable)c).compareTo(es[right]) > 0) {
                child = right;
                c = es[right];
            }

            if (key.compareTo(c) <= 0) {
                break;
            }

            es[k] = c;
        }

        es[k] = key;
    }

    private static <T> void siftDownUsingComparator(int k, T x, Object[] es, int n, Comparator<? super T> cmp) {
        int child;
        for(int half = n >>> 1; k < half; k = child) {
            child = (k << 1) + 1;
            Object c = es[child];
            int right = child + 1;
            if (right < n && cmp.compare(c, es[right]) > 0) {
                child = right;
                c = es[right];
            }

            if (cmp.compare(x, c) <= 0) {
                break;
            }

            es[k] = c;
        }

        es[k] = x;
    }

    private void heapify() {
        Object[] es = this.queue;
        int n = this.size;
        int i = (n >>> 1) - 1;
        Comparator cmp;
        if ((cmp = this.comparator) == null) {
            while(i >= 0) {
                siftDownComparable(i, es[i], es, n);
                --i;
            }
        } else {
            while(i >= 0) {
                siftDownUsingComparator(i, es[i], es, n, cmp);
                --i;
            }
        }

    }

    public Comparator<? super E> comparator() {
        return this.comparator;
    }

    private void writeObject(ObjectOutputStream s) throws IOException {
        s.defaultWriteObject();
        s.writeInt(Math.max(2, this.size + 1));
        Object[] es = this.queue;
        int i = 0;

        for(int n = this.size; i < n; ++i) {
            s.writeObject(es[i]);
        }

    }

    private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
        s.defaultReadObject();
        s.readInt();
        SharedSecrets.getJavaObjectInputStreamAccess().checkArray(s, Object[].class, this.size);
        Object[] es = this.queue = new Object[Math.max(this.size, 1)];
        int i = 0;

        for(int n = this.size; i < n; ++i) {
            es[i] = s.readObject();
        }

        this.heapify();
    }

    public final Spliterator<E> spliterator() {
        return new PriorityQueue.PriorityQueueSpliterator(0, -1, 0);
    }

    public boolean removeIf(Predicate<? super E> filter) {
        Objects.requireNonNull(filter);
        return this.bulkRemove(filter);
    }

    public boolean removeAll(Collection<?> c) {
        Objects.requireNonNull(c);
        return this.bulkRemove((e) -> {
            return c.contains(e);
        });
    }

    public boolean retainAll(Collection<?> c) {
        Objects.requireNonNull(c);
        return this.bulkRemove((e) -> {
            return !c.contains(e);
        });
    }

    private static long[] nBits(int n) {
        return new long[(n - 1 >> 6) + 1];
    }

    private static void setBit(long[] bits, int i) {
        bits[i >> 6] |= 1L << i;
    }

    private static boolean isClear(long[] bits, int i) {
        return (bits[i >> 6] & 1L << i) == 0L;
    }

    private boolean bulkRemove(Predicate<? super E> filter) {
        int expectedModCount = ++this.modCount;
        Object[] es = this.queue;
        int end = this.size;

        int i;
        for(i = 0; i < end && !filter.test(es[i]); ++i) {
        }

        if (i >= end) {
            if (this.modCount != expectedModCount) {
                throw new ConcurrentModificationException();
            } else {
                return false;
            }
        } else {
            int beg = i;
            long[] deathRow = nBits(end - i);
            deathRow[0] = 1L;
            ++i;

            for(; i < end; ++i) {
                if (filter.test(es[i])) {
                    setBit(deathRow, i - beg);
                }
            }

            if (this.modCount != expectedModCount) {
                throw new ConcurrentModificationException();
            } else {
                int w = beg;

                for(i = beg; i < end; ++i) {
                    if (isClear(deathRow, i - beg)) {
                        es[w++] = es[i];
                    }
                }

                for(i = this.size = w; i < end; ++i) {
                    es[i] = null;
                }

                this.heapify();
                return true;
            }
        }
    }

    public void forEach(Consumer<? super E> action) {
        Objects.requireNonNull(action);
        int expectedModCount = this.modCount;
        Object[] es = this.queue;
        int i = 0;

        for(int n = this.size; i < n; ++i) {
            action.accept(es[i]);
        }

        if (expectedModCount != this.modCount) {
            throw new ConcurrentModificationException();
        }
    }

    final class PriorityQueueSpliterator implements Spliterator<E> {
        private int index;
        private int fence;
        private int expectedModCount;

        PriorityQueueSpliterator(int origin, int fence, int expectedModCount) {
            this.index = origin;
            this.fence = fence;
            this.expectedModCount = expectedModCount;
        }

        private int getFence() {
            int hi;
            if ((hi = this.fence) < 0) {
                this.expectedModCount = PriorityQueue.this.modCount;
                hi = this.fence = PriorityQueue.this.size;
            }

            return hi;
        }

        public PriorityQueue<E>.PriorityQueueSpliterator trySplit() {
            int hi = this.getFence();
            int lo = this.index;
            int mid = lo + hi >>> 1;
            return lo >= mid ? null : PriorityQueue.this.new PriorityQueueSpliterator(lo, this.index = mid, this.expectedModCount);
        }

        public void forEachRemaining(Consumer<? super E> action) {
            if (action == null) {
                throw new NullPointerException();
            } else {
                if (this.fence < 0) {
                    this.fence = PriorityQueue.this.size;
                    this.expectedModCount = PriorityQueue.this.modCount;
                }

                Object[] es = PriorityQueue.this.queue;
                int i = this.index;

                int hi;
                Object e;
                for(this.index = hi = this.fence; i < hi && (e = es[i]) != null; ++i) {
                    action.accept(e);
                }

                if (PriorityQueue.this.modCount != this.expectedModCount) {
                    throw new ConcurrentModificationException();
                }
            }
        }

        public boolean tryAdvance(Consumer<? super E> action) {
            if (action == null) {
                throw new NullPointerException();
            } else {
                if (this.fence < 0) {
                    this.fence = PriorityQueue.this.size;
                    this.expectedModCount = PriorityQueue.this.modCount;
                }

                int i;
                if ((i = this.index) < this.fence) {
                    this.index = i + 1;
                    Object e;
                    if ((e = PriorityQueue.this.queue[i]) != null && PriorityQueue.this.modCount == this.expectedModCount) {
                        action.accept(e);
                        return true;
                    } else {
                        throw new ConcurrentModificationException();
                    }
                } else {
                    return false;
                }
            }
        }

        public long estimateSize() {
            return (long)(this.getFence() - this.index);
        }

        public int characteristics() {
            return 16704;
        }
    }

    private final class Itr implements Iterator<E> {
        private int cursor;
        private int lastRet = -1;
        private ArrayDeque<E> forgetMeNot;
        private E lastRetElt;
        private int expectedModCount;

        Itr() {
            this.expectedModCount = PriorityQueue.this.modCount;
        }

        public boolean hasNext() {
            return this.cursor < PriorityQueue.this.size || this.forgetMeNot != null && !this.forgetMeNot.isEmpty();
        }

        public E next() {
            if (this.expectedModCount != PriorityQueue.this.modCount) {
                throw new ConcurrentModificationException();
            } else if (this.cursor < PriorityQueue.this.size) {
                return PriorityQueue.this.queue[this.lastRet = this.cursor++];
            } else {
                if (this.forgetMeNot != null) {
                    this.lastRet = -1;
                    this.lastRetElt = this.forgetMeNot.poll();
                    if (this.lastRetElt != null) {
                        return this.lastRetElt;
                    }
                }

                throw new NoSuchElementException();
            }
        }

        public void remove() {
            if (this.expectedModCount != PriorityQueue.this.modCount) {
                throw new ConcurrentModificationException();
            } else {
                if (this.lastRet != -1) {
                    E moved = PriorityQueue.this.removeAt(this.lastRet);
                    this.lastRet = -1;
                    if (moved == null) {
                        --this.cursor;
                    } else {
                        if (this.forgetMeNot == null) {
                            this.forgetMeNot = new ArrayDeque();
                        }

                        this.forgetMeNot.add(moved);
                    }
                } else {
                    if (this.lastRetElt == null) {
                        throw new IllegalStateException();
                    }

                    PriorityQueue.this.removeEq(this.lastRetElt);
                    this.lastRetElt = null;
                }

                this.expectedModCount = PriorityQueue.this.modCount;
            }
        }
    }
}
