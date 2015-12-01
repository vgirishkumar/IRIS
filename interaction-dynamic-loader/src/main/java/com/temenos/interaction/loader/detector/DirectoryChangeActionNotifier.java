package com.temenos.interaction.loader.detector;

/*
 * #%L
 * interaction-dynamic-loader
 * %%
 * Copyright (C) 2012 - 2015 Temenos Holdings N.V.
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */
import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import com.temenos.interaction.core.loader.Action;
import com.temenos.interaction.core.loader.FileEvent;

/**
 * TODO: Document me!
 *
 * @author andres
 *
 */
public class DirectoryChangeActionNotifier implements DirectoryChangeDetector<Action<FileEvent<File>>> {

    private Collection<? extends File> resources = new ArrayList();
    private Collection<? extends Action<FileEvent<File>>> listeners = new ArrayList();
    private WatchService watchService;
    private ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
    private ScheduledFuture<?> scheduledTask = null;

    @Override
    public void setResources(Collection<? extends File> resources) {
        if (resources == null) {
            this.resources = new ArrayList<File>();
        }
        this.resources = new ArrayList<File>(resources);
        initWatchers(resources);
    }

    @Override
    public void setListeners(Collection<? extends Action<FileEvent<File>>> listeners) {
        if (listeners == null) {
            this.listeners = new ArrayList<Action<FileEvent<File>>>();
            return;
        }
        this.listeners = new ArrayList<Action<FileEvent<File>>>(listeners);
        initWatchers(getResources());
    }

    public Collection<? extends File> getResources() {
        return resources;
    }

    public Collection<? extends Action<FileEvent<File>>> getListeners() {
        return listeners;
    }

    protected void initWatchers(Collection<? extends File> resources) {
        if (scheduledTask != null) {
            scheduledTask.cancel(true);
        }
        if (resources == null || resources.isEmpty() || getListeners() == null || getListeners().isEmpty()) {
            return;
        }
        try {
            WatchService ws = FileSystems.getDefault().newWatchService();
            for (File file : resources) {
                Path filePath = Paths.get(file.toURI());
                filePath.register(ws, StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_MODIFY, StandardWatchEventKinds.ENTRY_DELETE);
            }

            watchService = ws;
            scheduledTask = executorService.scheduleWithFixedDelay(new ListenerNotificationTask(watchService, getListeners()), 10, 10, TimeUnit.SECONDS);
        } catch (IOException ex) {
            throw new RuntimeException("Error configuring directory change listener - unexpected IOException", ex);
        }
    }

    protected static class ListenerNotificationTask implements Runnable {

        private WatchService watchService;
        private Collection<? extends Action<FileEvent<File>>> listeners;

        public ListenerNotificationTask(WatchService watchService, Collection<? extends Action<FileEvent<File>>> listeners) {
            this.watchService = watchService;
            this.listeners = listeners;
        }

        @Override
        public void run() {
            try {
                WatchKey key = watchService.take(); //3.
                for (WatchEvent<?> e : key.pollEvents()) { //4.
                    WatchEvent.Kind<?> kind = e.kind();
                    if (kind != StandardWatchEventKinds.OVERFLOW) {
                        Path dir = (Path) key.watchable();
                        Path fullPath = dir.resolve((Path) e.context());
                        FileEvent<File> newEvent = new DirectoryChangeEvent(fullPath.toFile());
                        for (Action<FileEvent<File>> action : listeners) {
                            action.execute(newEvent);
                        }
                    }
                }
                key.reset();
            } catch (InterruptedException ex) {

            }
        }

    }

    public static class DirectoryChangeEvent implements FileEvent<File> {

        private File directory;

        public DirectoryChangeEvent(File file) {
            if (!file.isDirectory()) {
                directory = file.getAbsoluteFile().getParentFile();
            } else {
                directory = file;
            }
        }

        @Override
        public File getResource() {
            return directory;
        }

    }

}
