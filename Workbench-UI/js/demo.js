$(document).ready(function () {

    var sendEndpoint = 'http://ec2-18-213-219-240.compute-1.amazonaws.com/cdc_service_stanford/request_cdc_stanford_service',
        receiveEndpoint = 'http://ec2-18-213-219-240.compute-1.amazonaws.com/cdc_service_stanford/fetch_cdc_stanford_service_result/',
        data = '';

    $("input[name='pipeline']").change(function () {
        if ($(this).val() === 'stanford') {
            $('.stanford-pipeline').show();
            $('.opennlp-pipeline').hide();
            $('.gate-pipeline').hide();
            $('.ctakes-pipeline').hide();

            $('.selected-workflow').html('Stanford');

            sendEndpoint = 'http://ec2-18-213-219-240.compute-1.amazonaws.com/cdc_service_stanford/request_cdc_stanford_service';
            receiveEndpoint = 'http://ec2-18-213-219-240.compute-1.amazonaws.com/cdc_service_stanford/fetch_cdc_stanford_service_result/';
        } else if ($(this).val() === 'opennlp') {
            $('.stanford-pipeline').hide();
            $('.opennlp-pipeline').show();
            $('.gate-pipeline').hide();
            $('.ctakes-pipeline').hide();

            $('.selected-workflow').html('Open NLP');

            sendEndpoint = 'http://ec2-18-213-219-240.compute-1.amazonaws.com/cdc_service_opennlp/request_cdc_opennlp_service';
            receiveEndpoint = 'http://ec2-18-213-219-240.compute-1.amazonaws.com/cdc_service_opennlp/fetch_cdc_opennlp_service_result/';
        } else if ($(this).val() === 'gate') {
            $('.stanford-pipeline').hide();
            $('.opennlp-pipeline').hide();
            $('.gate-pipeline').show();
            $('.ctakes-pipeline').hide();

            $('.selected-workflow').html('Gate');

            sendEndpoint = 'http://ec2-18-213-219-240.compute-1.amazonaws.com/cdc_service_opennlp/request_cdc_gate_service';
            receiveEndpoint = 'http://ec2-18-213-219-240.compute-1.amazonaws.com/cdc_service_opennlp/request_cdc_gate_service/';
        } else {
            $('.stanford-pipeline').hide();
            $('.opennlp-pipeline').hide();
            $('.gate-pipeline').hide();
            $('.ctakes-pipeline').show();

            $('.selected-workflow').html('cTakes');

            //TODO Make this work
            sendEndpoint = 'http://ec2-18-213-219-240.compute-1.amazonaws.com/cdc_service_gate/request_cdc_ctakes_service';
            receiveEndpoint = 'http://ec2-18-213-219-240.compute-1.amazonaws.com/cdc_service_gate/fetch_cdc_ctakes_service_result/';
        }
    });    

    $(".input-select").change(function () {
        if ($(this).val() === '1') {
            $('.file-input-1').show();
            $('.my-input').hide();

            $('.selected-file-name').html('HL7Sample');
            data = $('#my-input1').html();
            
        } else if ($(this).val() === '2') {
            $('.file-input-1').hide();
            $('.my-input').show();

            $('.selected-file-name').html('My input file');
        } 
    });
    var status = false;
    $('.hs-pipeline-description').click(function () {
        if (status) {
            $('.stanford-pipeline').hide();
            $('.opennlp-pipeline').hide();
            $('.gate-pipeline').hide();
            $('.ctakes-pipeline').hide();
            status = false;
            $(this).html('Show Pipeline Description');
        } else {
            status = true;
            $(this).html('Hide Pipeline Description');
            var pipeline_value = $("input[name='pipeline']").val();
            if (pipeline_value == 'stanford') {
                $('.stanford-pipeline').show();
                $('.opennlp-pipeline').hide();
                $('.gate-pipeline').hide();
                $('.ctakes-pipeline').hide();
            } else if (pipeline_value == 'opennlp') {
                $('.stanford-pipeline').hide();
                $('.opennlp-pipeline').show();
                $('.gate-pipeline').hide();
                $('.ctakes-pipeline').hide();
            } else if (pipeline_value == 'gate') {
                $('.stanford-pipeline').hide();
                $('.opennlp-pipeline').hide();
                $('.gate-pipeline').show();
                $('.ctakes-pipeline').hide();
            } else {
                $('.stanford-pipeline').hide();
                $('.opennlp-pipeline').hide();
                $('.gate-pipeline').hide();
                $('.ctakes-pipeline').show();
            }
        }
    });

    $('.run-demo').on('click', function () {
        if ($(".input-select").val() == '2') {
            data = $('#my-input2').val();
        } else {
            data = $('#my-input1').html();
        }
        console.log(data);
        $.ajax({
            url: sendEndpoint,
            data: data,
            type: 'POST',
        }).done(function (response) {
            console.log(response);
        })
            .fail(function (error) {
                console.log(error);
        });

    });

});