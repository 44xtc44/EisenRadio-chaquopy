import os
import unittest.mock as mock

import eisenradio
import eisenradio.db
import ghettorecorder.ghetto_recorder as ghetto_recorder  # only to show pkg home path on device
import eisenradio.lib.platform_helper as platform_helper

from eisenradio import gui
from eisenradio.lib.eisdb import status_read_status_set

glob_db_path = None
""" Flask application factory with blueprints
'Home' and 'Util' load modules, templates, styles, favicon from its own project folders
"""
import certifi
from flask import Flask
from os import path, environ
from eisenradio.api import api, eisenApi

# android ssl fix
environ['SSL_CERT_FILE'] = certifi.where()

script_path = path.dirname(__file__)

is_android_device = 'ANDROID_STORAGE' in os.environ
glob_db_path = "/data/data/com.hornr/EisenRadio/eisenradio_android.db"  # Mock the database path static or call main(db_path)


def create_app(work_port):
    """ flask prod application factory
    prod
    """
    app = Flask('eisenradio')

    with app.app_context():

        api.init_app(app)
        eisenApi.init_work_port(work_port)  # port to use; browser autostart, sound endpoint

        is_snap_device = 'SNAP' in environ  # write in [SNAP_USER_COMMON]
        is_android_device = 'ANDROID_STORAGE' in environ

        if not is_snap_device and not is_android_device:
            # PROD
            app.config.from_object('config.ProdConfig')
            # app.config.from_object('config.TestConfig')    # total fail from pytest

        if is_snap_device:
            # write_config('snap')
            # remove_config(environ["SNAP_USER_COMMON"])
            app.config.from_object('config.SnapConfig')
            pass

        if is_android_device:
            app.config.from_object('config.AndroidConfig')
            print(f"\n\t << org >> app.config['DATABASE'] {app.config['DATABASE']}")

        # helper stuff
        from eisenradio.lib.platform_helper import main as start_frontend
        from eisenradio.lib.eisdb import install_new_db as create_install_db

        from eisenradio.eisenhome import routes as home_routes
        from eisenradio.eisenutil import routes as util_routes

        # Register Blueprints (pointer to parts of the application, subprojects in production)
        app.register_blueprint(home_routes.eisenhome_bp)
        app.register_blueprint(util_routes.eisenutil_bp)

        if is_android_device:
            app.config["DATABASE"] = "/data/data/com.hornr/EisenRadio/eisenradio_android.db"  # Mock the database path in flask's app.config
        else:
            app.config["DATABASE"] = glob_db_path

        create_install_db(app.config["DATABASE"])
        print(f"\n\t << mock >> app.config['DATABASE'] {app.config['DATABASE']}")
        print(f"\n\t << ghetto_path >> {ghetto_recorder.__file__}")  # show pkg location; seems to be only a link

        start_frontend()
        return app


def main_mock(db_path=None):
    """ Mock EisenRadio functions to run with Android.

    GhettoRecorder *ghetto_recorder.py* was patched and we build wheel with Chaquopy, same version of
    EisenRadio requirement. Put patched GhettoRecorder pkg as first *install* in gradle pip.
    signal.signal (Ctrl+C end app) was removed, else we are denied to run outside the main thread.
    EisenRadio then uses the already installed version.

    global var is not working in Java jni, needs direct mock of var in fun

    :param str db_path: complete path to db file, if called from external module -----> not working in Android
    """
    if not is_android_device:
        global glob_db_path
        if db_path:
            glob_db_path = db_path

    with mock.patch.object(eisenradio, "create_app", create_app):  # patch DB path
        eisenradio.gui.main()


if __name__ == "__main__":
    main_mock()

