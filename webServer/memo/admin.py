from django.contrib import admin
from .models import Memo

# Register your models here.
# 관리자 창에서 관리하기 위해 등록.
admin.site.register(Memo)