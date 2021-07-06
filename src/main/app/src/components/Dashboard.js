import React, { useState, useEffect, useRef } from 'react';
import { DataTable } from 'primereact/datatable';
import { Column } from 'primereact/column';
import ApiService from '../service/ApiService';
import { Toast } from 'primereact/toast';
import { Button } from 'primereact/button';
import { FileUpload } from 'primereact/fileupload';
import { Toolbar } from 'primereact/toolbar';
import { InputText } from 'primereact/inputtext';
import '../css/DataTableDemo.css';
import 'primeflex/primeflex.css';
import { connect } from 'react-redux'

const DashBaord = (props) => {

    const timeLabel = {
        background: '#007bff',
        textAlign: 'center',
        width: '41.62vh',
        paddingTop: '10px',
        paddingBottom: '10px',
        borderRadius: '3px',
        marginBottom: '20px',
        color: 'white'
    }
    let emptyWebservice = {
        id: null,
        name: '',
        url: '',
        status: null
    };

    let emptyLastUpdate = {
        hour: null,
        time: null,
        second: null
    };

    let initalRefreshRate = {
        minute_ms: 5000
    }

    const [webServices, setWebServices] = useState(null);
    const [lastUpdate, setLastUpdate] = useState(emptyLastUpdate);



    const [webServiceDialog, setWebServiceDialog] = useState(false);
    const [deletewebServiceDialog, setDeletewebServiceDialog] = useState(false);
    const [deletewebServicesDialog, setDeleteWebServicesDialog] = useState(false);
    const [webService, setWebService] = useState(emptyWebservice);
    const [selectedWebServices, setSelectedWebServices] = useState(null);
    const [submitted, setSubmitted] = useState(false);
    const [globalFilter, setGlobalFilter] = useState(null);
    const toast = useRef(null);
    const dt = useRef(null);
    const apiService = new ApiService();

    //const MINUTE_MS = 5000;

    useEffect(() => {

        if (!localStorage.getItem("token")) {
            return;
        }
         apiService.getApiStatus(localStorage.getItem("token")).then(data => setWebServices(data))
                        .catch(error => console.log("Error while trying to make api call " + JSON.stringify(error)));

    }, []); // eslint-disable-line react-hooks/exhaustive-deps

    const createId = () => {
        let id = '';
        let chars = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789';
        for (let i = 0; i < 5; i++) {
            id += chars.charAt(Math.floor(Math.random() * chars.length));
        }
        return id;
    }

    const onInputChange = (e, name) => {
        const val = (e.target && e.target.value) || '';
        let _webService = { ...webService };
        _webService[`${name}`] = val;

        setWebService(_webService);
    }

    const exportCSV = () => {
        dt.current.exportCSV();
    }

    const confirmDeleteSelected = () => {
        setDeleteWebServicesDialog(true);
    }

    const statusBodyTemplate = (rowData) => {
        return <span className={rowData.statusCode == 200 ? 'green' : 'red'}>  {rowData.statusCode == 200 ? rowData.statusCode = 'UP / Running' : 'Down / No Response'}</span>;
    }

    const rightToolbarTemplate = () => {
        return (
            <React.Fragment>
                <FileUpload mode="basic" accept="image/*" maxFileSize={1000000} label="Import" chooseLabel="Import" className="p-mr-2 p-d-inline-block" />
                <Button label="Export" icon="pi pi-upload" className="p-button-help" onClick={exportCSV} />
            </React.Fragment>
        )
    }

    const header = (
        <div className="table-header">
            <h5 className="p-m-0">API Status</h5>
            <span className="p-input-icon-left">
                <i className="pi pi-search" />
                <InputText type="search" onInput={(e) => setGlobalFilter(e.target.value)} placeholder="Search..." />
            </span>
        </div>
    );

    return (
        <div className="datatable-crud-demo">
            <Toast ref={toast} />

            <div className="card">
                <Toolbar className="p-mb-4" right={rightToolbarTemplate}></Toolbar>
                

                <DataTable ref={dt} value={webServices}
                    dataKey="id" paginator rows={10} rowsPerPageOptions={[5, 10, 25]}
                    paginatorTemplate="FirstPageLink PrevPageLink PageLinks NextPageLink LastPageLink CurrentPageReport RowsPerPageDropdown"
                    currentPageReportTemplate="Showing {first} to {last} of {totalRecords} webServices"
                    globalFilter={globalFilter}
                    header={header}>

                    <Column field="name" header="Name" ></Column>
                    <Column field="url" header="Url" ></Column>
                    <Column field="statusCode" header="Status" body={statusBodyTemplate} ></Column>
                </DataTable>
            </div>
            <br /> <br /> <br /> <br />
        </div >
    );
}

const mapStateToProps = state => {
    return {
        state
    }
}

const mapDispatchToProps = dispatch => {
    return {
        dispatch
    }
}

export default connect(
    mapStateToProps,
    mapDispatchToProps
)(DashBaord)