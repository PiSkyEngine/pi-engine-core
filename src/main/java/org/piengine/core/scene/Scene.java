/*
 * MIT License
 * 
 * Copyright (c) 2025 Sly Technologies Inc
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package org.piengine.core.scene;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;

import org.piengine.util.concurrent.locks.Lockable;
import org.piengine.util.concurrent.locks.UpgradableReadWriteLock;
import org.piengine.util.concurrent.locks.Lockable.LockableSupport;
import org.piengine.util.concurrent.locks.Locked.LockedSupport;

/**
 * Represents a scenegraph, loaded from a .pis file. Contains nodes (meshes,
 * lights, cameras).
 */
public final class Scene {

	/**
	 * The Class ReadLocked.
	 */
	final class ReadLocked extends LockedSupport {
		
		/**
		 * Instantiates a new read locked.
		 *
		 * @param lock the lock
		 */
		public ReadLocked(Lock lock) {
			super(lock);
		}
	}

	/**
	 * The Class WriteLocked.
	 */
	final class WriteLocked extends LockedSupport {
		
		/**
		 * Instantiates a new write locked.
		 *
		 * @param lock the lock
		 */
		public WriteLocked(Lock lock) {
			super(lock);
		}
	}

	/** The nodes. */
	private final List<SceneNode> nodes = new ArrayList<>();

	/** The rw lock. */
	private final ReadWriteLock rwLock = new UpgradableReadWriteLock();
	
	/** The r locked. */
	private final ReadLocked rLocked = new ReadLocked(rwLock.readLock());
	
	/** The w locked. */
	private final WriteLocked wLocked = new WriteLocked(rwLock.writeLock());
	
	/** The lockable. */
	private final Lockable<ReadLocked, WriteLocked> lockable = new LockableSupport<>(rwLock, rLocked, wLocked);

	/** The name. */
	private String name;

	/**
	 * Instantiates a new scene.
	 *
	 * @param name the name
	 */
	public Scene(String name) {
		this.name = name;
	}

	/**
	 * Adds the node.
	 *
	 * @param node the node
	 * @return true, if successful
	 */
	public boolean addNode(SceneNode node) {
		return nodes.add(node);
	}

	/**
	 * Removes the node.
	 *
	 * @param node the node
	 * @return true, if successful
	 */
	public boolean removeNode(SceneNode node) {
		return nodes.remove(node);
	}

	/**
	 * Gets the nodes.
	 *
	 * @return the nodes
	 */
	public List<SceneNode> getNodes() {
		return nodes;
	}

	/**
	 * Lock for read.
	 *
	 * @return the read locked
	 * @throws InterruptedException the interrupted exception
	 */
	public ReadLocked lockForRead() throws InterruptedException {
		return lockable.lockForRead();
	}

	/**
	 * Lock for write.
	 *
	 * @return the write locked
	 * @throws InterruptedException the interrupted exception
	 */
	public WriteLocked lockForWrite() throws InterruptedException {
		return lockable.lockForWrite();
	}

	/**
	 * Name.
	 *
	 * @return the string
	 */
	public String name() {
		return name;
	}

	/**
	 * Lockable.
	 *
	 * @return the lockable
	 */
	public Lockable<ReadLocked, WriteLocked> lockable() {
		return lockable;
	}
}