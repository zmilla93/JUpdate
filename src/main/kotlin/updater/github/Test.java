package updater.github;

import io.github.zmilla93.jupdate.AbstractUpdater;

public class Test extends AbstractUpdater {



    @Override
    public boolean isUpdateAvailable() {
        return false;
    }

    @Override
    public boolean download() {
        return false;
    }

    @Override
    public boolean unpack() {
        return false;
    }

    @Override
    public boolean patch() {
        return false;
    }

    @Override
    public boolean clean() {
        return false;
    }

}
