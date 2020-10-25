# -*- coding: UTF-8 -*-
# logger.py
#　　Usage:
# from pathlib import Path
# sys.path.append('%s' % Path(__file__).parent.parent.resolve())
# from aaa.logger import Logger
#  ロガー実行名として出力
# logger = Logger('sample')
#

from logging import Formatter, handlers, StreamHandler, getLogger, DEBUG, WARN, INFO
# import inspect
import re
from datetime import datetime, timedelta
import os

class Logger:
    def __init__(self, name=None):
        # if name=="{}.logger".format(__package__):
        #     fsplits = inspect.stack()[1].filename.split('/')
        #     name = fsplits[len(fsplits)-1]
        if name==None: raise RuntimeError('name は必須です')
        # ロガー生成
        self.logger = getLogger(name)
        self.logger.setLevel(DEBUG)
        formatter = Formatter(fmt="%(asctime)s.%(msecs)03d %(levelname)7s %(message)s [%(name)s  %(processName)s - %(threadName)s]",
                              datefmt="%Y/%m/%d %H:%M:%S")
        # 時刻ローテーション
        handler = handlers.TimedRotatingFileHandler(filename='/var/log/test.log',
                                                    encoding='UTF-8',
                                                    when='D',
                                                    backupCount=7)
        handler.namer = lambda x: re.sub(r"\.([^.]+)\.(\d{4})-(\d{2})-(\d{2})$", r"_\2-\3-\4.\1", x)
        # old log delete
        delfile = datetime.strftime((lambda x=datetime.now(): x - timedelta(days=7))(), '/var/log/test_%Y-%m-%d.log')
        if os.path.exists(delfile): os.remove(delfile)
        # サイズローテーション
        ''' 
       handler = handlers.RotatingFileHandler(filename='/var/log/test.log',
                                               encoding='UTF-8',
                                               maxBytes=1048576,
                                               backupCount=3)
       '''
        # ログファイル設定
        handler.setLevel(INFO)
        handler.setFormatter(formatter)
        self.logger.addHandler(handler)
        # 標準出力用 設定： DEBUG レベルまで標準出力する
        sthandler = StreamHandler()
        sthandler.setLevel(DEBUG)
        sthandler.setFormatter(formatter)
        self.logger.addHandler(sthandler)

    def debug(self, msg):
        self.logger.debug(msg)
    def info(self, msg):
        self.logger.info(msg)
    def warn(self, msg):
        self.logger.warning(msg)
    def error(self, msg):
        self.logger.error(msg)
    def critical(self, msg):
        self.logger.critical(msg)