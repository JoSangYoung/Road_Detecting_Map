from django.db import models

# Create your models here.
# class 만든걸 db에 등록하려면 python manage.py makemigrations
# 이후에 migrate 해야 db에 공간이 만들어짐.
class Memo(models.Model):
	lat = models.FloatField(default = 37.564214)
	long = models.FloatField(default = 127.001699)
	memo = models.TextField(blank=True)
	status = models.ImageField()