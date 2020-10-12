# -*- coding: UTF-8 -*-

from datetime import datetime
import time

# 現在エポック秒
epoch = int(time.mktime(datetime.now().timetuple()))
print(epoch)

# エポック秒 → datetime
dtime = datetime(*time.localtime(epoch)[:6])
print(dtime)

# 現在エポックミリ秒
epocmili = int(datetime.now().timestamp() * 1000)
print(epocmili)

row = [ 'a', 'b', '1602504503' ]
# print(row)
# print( row[len(row)-1] )
# dtime = datetime(*time.localtime(int(row[len(row)-1]))[:6])
# print(dtime)

row[len(row)-1] = "%s" % datetime(*time.localtime(int(row[len(row)-1]))[:6])
print(row)
