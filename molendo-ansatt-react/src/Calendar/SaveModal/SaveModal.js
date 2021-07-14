import React, { Component } from 'react'
import { Accordion, Button, Card, Modal, Form } from 'react-bootstrap'
import i18n from './../../Language/i18n'
import CaretDownIcon from './../../Icons/CaretDownIcon'
import './SaveModal.css'

export default class SaveModal extends Component {
    constructor(props) {
        super(props)
        this.state = {
            defaultClose: true
        }
        this.handleSave = this.handleSave.bind(this)
        this.handleDefaultClose = this.handleDefaultClose.bind(this)
    }

    handleDefaultClose() {
        this.setState({defaultClose: !this.state.defaultClose})
    }

    handleSave() {
        this.setState({loading: true})
        this.props.saveRosters().then(async response => {
            let msg = await response.text()
            if (response.status == 201 || response.status == 200) {
                if (this.state.defaultClose) this.props.toggleModal()
                // SaveRoster > 201; DeleteRoster > 200
                let status = response.status + " - "+response.statusText
                this.setState({success: true, status: status})
                this.props.getRosters(null, null)
            } else {
                let status = response.status + " - "+response.statusText
                this.setState({status: status})
            }
            this.setState({loading: false, message: msg})
        })
    }

    render() {
        let body = [<p key="1">{i18n.t('save.intro-title')}</p>,
                        <p key="2">{i18n.t('save.intro-subtitle')}</p>]
        if (this.state.success && this.state.status) {
            body = [<p key="1">{i18n.t('save.save-successful')}</p>,
                        <p key="2">{i18n.t('save.code')} {this.state.status}</p>]
        } else if (this.state.status && !this.state.success) {
            body = [<p key="1">{i18n.t('save.save-failed')}</p>,
                        <p key="2">{i18n.t('save.code')} {this.state.status}</p>]
        } else if (this.state.loading) {
            body = [<p className="loading" key="1">{i18n.t('save.loading-title')}</p>,
                    <p key="2">{i18n.t('save.loading-subtitle')}</p>]
        }

        let message = 
            <Accordion>
                <Card>
                    <Accordion.Toggle as={Card.Header} eventKey="0">
                    {i18n.t('save.details')}<CaretDownIcon />
                    </Accordion.Toggle>
                    <Accordion.Collapse eventKey="0">
                        <Card.Body>{this.state.message}</Card.Body>
                    </Accordion.Collapse>
                </Card>
            </Accordion>

        let defaultClose = (
            <Form>
                <Form.Check
                    id="defaultCloseCheck"
                    onChange={this.handleDefaultClose}
                    type="checkbox"
                    checked={this.state.defaultClose}
                    label={i18n.t('save.if-successful')}/>
            </Form>
        )
        

        return (
            <Modal className={`saveModal ${this.props.cssTheme}`}
                show={this.props.show}
                onHide={this.props.toggleModal} >

                <Modal.Header closeButton>
                    <Modal.Title>{i18n.t('save.header')}</Modal.Title>
                </Modal.Header>

                <Modal.Body>
                    {body}
                    {this.state.message && message}
                </Modal.Body>

                <Modal.Footer>
                    {defaultClose}
                    <Button
                        className={this.state.success ? "success" : "confirm"}
                        disabled={this.state.success || this.state.loading }
                        variant="secondary"
                        onClick={this.handleSave}>
                        {this.state.success ? i18n.t('save.saved') : i18n.t('save.save')}
                        </Button>

                    <Button
                        className="error"
                        disabled={this.state.loading}
                        variant="secondary"
                        onClick={this.props.toggleModal}>
                        {i18n.t('buttons.close')}
                        </Button>

                </Modal.Footer>
            </Modal>
        )
    }
}
