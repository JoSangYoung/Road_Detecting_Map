from django.urls import path
from . import views				# views 사용하기 위해 import

urlpatterns = [
	path('', views.memo),		# localhost로 들어왔을 때 views.memo 함수 실행
]