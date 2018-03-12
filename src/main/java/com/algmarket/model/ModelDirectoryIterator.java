package com.algmarket.model;

import com.algmarket.APIException;

import java.util.ArrayList;
import java.util.List;

public class ModelDirectoryIterator extends AbstractModelIterator<ModelDirectory> {
    public ModelDirectoryIterator(ModelDirectory dir) {
        super(dir);
    }

    protected void loadNextPage() throws APIException {
        List<String> dirnames = new ArrayList<String>();
        ModelDirectory.DirectoryListResponse response = dir.getPage(marker, false);

        if (response.folders != null) {
            for(ModelDirectory.DirectoryMetadata meta : response.folders) {
                dirnames.add(meta.name);
            }
        }

        // Update iterator state
        setChildrenAndMarker(dirnames, response.marker);
    }

    protected ModelDirectory newDataObjectInstance(String dataUri) {
        return new ModelDirectory(dir.client, dataUri);
    }
}
