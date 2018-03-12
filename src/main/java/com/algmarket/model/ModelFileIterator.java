package com.algmarket.model;

import com.algmarket.APIException;

import java.util.ArrayList;
import java.util.List;

public class ModelFileIterator extends AbstractModelIterator<ModelFile> {
    public ModelFileIterator(ModelDirectory dir) {
        super(dir);
    }

    protected void loadNextPage() throws APIException {
        List<String> filenames = new ArrayList<String>();
        ModelDirectory.DirectoryListResponse response = dir.getPage(marker, false);

        if (response.files != null) {
            for(ModelDirectory.FileMetadata meta : response.files) {
                filenames.add(meta.filename);
            }
        }

        // Update iterator state
        setChildrenAndMarker(filenames, response.marker);
    }

    protected ModelFile newDataObjectInstance(String dataUri) {
        return new ModelFile(dir.client, dataUri);
    }
}
