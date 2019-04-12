# from sample.forms import SampleForm

# from django.views.generic import FormView

# class SampleFormView(FormView):
    # form_class = SampleForm
    # template_name = "sample/index.html"

from django.shortcuts import render
from sample.models import SampleModel
	
def mapviews(request):
	samplemodels = SampleModel.objects.all()
	context = {'samplemodels': samplemodels}
	return render(request, 'sample/index.html', context)