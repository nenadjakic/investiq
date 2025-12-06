import { CommonModule } from "@angular/common";
import { Component, HostListener, OnInit } from "@angular/core";
import { MatButtonModule } from "@angular/material/button";
import { MatIconModule } from "@angular/material/icon";
import { MatNavList, MatListItem } from "@angular/material/list";
import { MatSidenavModule } from "@angular/material/sidenav";
import { MatToolbarModule } from "@angular/material/toolbar";
import { ActivatedRoute, NavigationEnd, Router, RouterModule } from "@angular/router";
import { filter } from "rxjs";

interface Link {
  label: string;
  url: string;
  activated?: boolean;
}

@Component({
    selector: 'app-layout',
    standalone: true,
    imports: [
        CommonModule,
        RouterModule,
        MatSidenavModule,
        MatToolbarModule,
        MatIconModule,
        MatButtonModule,
        MatNavList,
        MatListItem

    ],
    templateUrl: './layout.html'
})
export class Layout implements OnInit {
    protected prefixTitle = 'investIQ';
    protected title = 'investIQ';
    links = [
        { label: 'Dashboard', url: '/dashboard', icon: 'dashboard', activated: false },
        { label: 'Portfolio', url: '/portfolio', icon: 'savings', activated: false },
        { label: 'Assets', url: '/asset', icon: 'diamond', activated: false },
        { label: 'Transactions', url: '/transaction', icon: 'receipt', activated: false },
        { label: 'Settings', url: '/settings', icon: 'settings',  activated: false },
    ];

    constructor(private router: Router, private activatedRoute: ActivatedRoute) { }

    ngOnInit(): void {
        this.router.events.pipe(
            filter(event => event instanceof NavigationEnd)
        ).subscribe(() => {
            let route = this.activatedRoute;
            while (route.firstChild) {
                route = route.firstChild;
            }
            if (route.snapshot.data['title']) {
                this.title = this.prefixTitle + ' ::: ' + route.snapshot.data['title'];
            } else {
                this.title = this.prefixTitle;
            }
        });
    }
}