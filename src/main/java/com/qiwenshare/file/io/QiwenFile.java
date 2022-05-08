package com.qiwenshare.file.io;

import com.qiwenshare.common.exception.QiwenException;
import com.qiwenshare.ufop.util.UFOPUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * @author MAC
 * @version 1.0
 * @description: TODO
 * @date 2022/4/21 12:08
 */
public class QiwenFile {

    private final String path;
    public static final String separator = "/";
    private boolean isDirectory;

    public QiwenFile(String pathname, boolean isDirectory) {
        if (StringUtils.isEmpty(pathname)) {
            throw new QiwenException("file name format error，pathname:" + pathname);
        }
        this.path = formatPath(pathname);
        this.isDirectory = isDirectory;
    }

    public QiwenFile(String parent, String child, boolean isDirectory) {
        if (StringUtils.isEmpty(child)) {
            throw new QiwenException("file name format error，parent:" + parent +", child:" + child);
        }
        if (parent != null) {
            String parentPath = separator.equals(formatPath(parent)) ? "" : formatPath(parent);
            String childPath = formatPath(child);
            if (childPath.startsWith(separator)) {
                childPath = childPath.replaceFirst(separator, "");
            }
            this.path = parentPath + separator + childPath;
        } else {
            this.path = formatPath(child);
        }
        this.isDirectory = isDirectory;
    }

    public static String formatPath(String path) {
        path = UFOPUtils.pathSplitFormat(path);
        if ("/".equals(path)) {
            return path;
        }
        if (path.endsWith("/")) {
            int length = path.length();
            return path.substring(0, length - 1);
        }

        return path;
    }

    public String getParent() {
        if (separator.equals(this.path)) {
            return null;
        }
        if (!this.path.contains("/")) {
            return null;
        }
        int index = path.lastIndexOf(separator);
        if (index == 0) {
            return separator;
        }
        return path.substring(0, index);
    }

    public QiwenFile getParentFile() {
        String parentPath = this.getParent();
        return new QiwenFile(parentPath, true);
    }

    public String getName() {
        int index = path.lastIndexOf(separator);
        if (!path.contains(separator)) {
            return path;
        }
        return path.substring(index + 1);
    }

    public String getExtendName() {
        return FilenameUtils.getExtension(getName());
    }

    public String getNameNotExtend() {
        return FilenameUtils.removeExtension(getName());
    }

    public String getPath() {
        return path;
    }

    public boolean isDirectory() {
       return isDirectory;
    }

    public boolean isFile() {
        return !isDirectory;
    }

    
}
