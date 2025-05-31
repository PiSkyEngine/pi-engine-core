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

import org.piengine.core.scene.Scene.ReadLocked;
import org.piengine.core.scene.Scene.WriteLocked;

/**
 * The Class SceneExample.
 *
 * @author Mark Bednarczyk [mark@slytechs.com]
 * @author Sly Technologies Inc.
 */
public class SceneExample {

	/** The scene. */
	private Scene scene = new Scene("main");

	/**
	 * The main method.
	 *
	 * @param args the arguments
	 * @throws InterruptedException the interrupted exception
	 */
	public void main(String[] args) throws InterruptedException {

        System.out.println("Example 1: Read lock");
        System.out.println(scene.lockable().toString());
		try (ReadLocked rscene = this.scene.lockForRead()) {

            System.out.println("Released read lock");
            System.out.println(scene.lockable().toString());
		} // Unlock read
        System.out.println(scene.lockable().toString());
        System.out.println(scene.lockable().toString());

        System.out.println("Example 2: Acquired read lock");
		try (ReadLocked rscene = scene.lockForRead()) {

            System.out.println("Upgraded to write lock (now holding both)");
            System.out.println(scene.lockable().toString());
			try (WriteLocked wlock = scene.lockForWrite()) { // Will block until gets write lock
                System.out.println("Released write lock");
                System.out.println(scene.lockable().toString());
			} // Unlocks write, downgrades to read

            System.out.println("Released read lock");
            System.out.println(scene.lockable().toString());
		} // Unlocks read

        System.out.println("Example 3: Acquired read lock");
		try (var rlock = this.scene.lockForRead()) {

            System.out.println("Upgraded to write lock (now holding both)");
            scene.lockForWrite(); // Will block until gets write lock
            System.out.println(scene.lockable().toString());

            System.out.println("Released both read and write lock");
            System.out.println(scene.lockable().toString());
		} // Unlocks both read and write
        System.out.println(scene.lockable().toString());

        System.out.println("Example 4: Acquired write lock");
		try (var wlock = this.scene.lockForWrite()) {

            System.out.println("Released write lock");
		} // Unlocks write
        System.out.println(scene.lockable().toString());

        System.out.println("Example 4: Acquired write lock");
		try (var wlock = this.scene.lockForWrite()) {
	        System.out.println("acquire read lock");
			try (var rlock = scene.lockForRead()) { // Does nothing

	            System.out.println("Released read lock");
			} // Does nothing

            System.out.println("Released write lock");
		} // Unlocks read write
        System.out.println(scene.lockable().toString());

        System.out.println("Example 5: Acquired write lock");
		try (var wlock = this.scene.lockForWrite()) {
	        System.out.println("acquire read lock");
	        System.out.println(scene.lockable().toString());
			try (var rlock = scene.lockForRead()) { // Does nothing

	            System.out.println("Released read lock");
	            System.out.println(scene.lockable().toString());
			} // Unlocks read, locks write

            System.out.println("Released write lock");
		} // Unlocks write
        System.out.println(scene.lockable().toString());

	}

}
