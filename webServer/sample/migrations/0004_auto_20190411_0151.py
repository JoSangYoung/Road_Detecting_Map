# Generated by Django 2.1.7 on 2019-04-10 16:51

from django.db import migrations, models


class Migration(migrations.Migration):

    dependencies = [
        ('sample', '0003_auto_20190411_0150'),
    ]

    operations = [
        migrations.AlterField(
            model_name='samplemodel',
            name='status',
            field=models.ImageField(upload_to=''),
        ),
    ]